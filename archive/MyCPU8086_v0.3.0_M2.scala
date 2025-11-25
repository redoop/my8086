package cpu8086

import chisel3._
import chisel3.util._

/**
 * MyCPU8086 Milestone 2: 基础功能完整
 * 
 * 新增功能:
 * - 条件跳转指令 (JE, JNE, JG, JL, JGE, JLE)
 * - 栈操作 (PUSH, POP)
 * - 子程序调用 (CALL, RET)
 * - 逻辑运算 (AND, OR, XOR, NOT, TEST)
 * - 更多 MOV 变体
 * - 完整的算术运算
 */
class MyCPU8086_M2 extends Module {
  val io = IO(new Bundle {
    val memAddr = Output(UInt(20.W))
    val memDataOut = Output(UInt(16.W))
    val memDataIn = Input(UInt(16.W))
    val memWrite = Output(Bool())
    val memRead = Output(Bool())
    val halt = Output(Bool())
    // 调试输出
    val ax = Output(UInt(16.W))
    val bx = Output(UInt(16.W))
    val cx = Output(UInt(16.W))
    val dx = Output(UInt(16.W))
    val sp = Output(UInt(16.W))
    val ip = Output(UInt(16.W))
    val flags = Output(UInt(16.W))
  })

  // 寄存器定义
  val ax = RegInit(0.U(16.W))
  val bx = RegInit(0.U(16.W))
  val cx = RegInit(0.U(16.W))
  val dx = RegInit(0.U(16.W))
  val sp = RegInit(0xFFFE.U(16.W))
  val bp = RegInit(0.U(16.W))
  val si = RegInit(0.U(16.W))
  val di = RegInit(0.U(16.W))
  
  // 段寄存器
  val cs = RegInit(0xFFFF.U(16.W))
  val ds = RegInit(0.U(16.W))
  val ss = RegInit(0.U(16.W))
  val es = RegInit(0.U(16.W))
  
  // 指令指针和标志寄存器
  val ip = RegInit(0.U(16.W))
  val flags = RegInit(0.U(16.W))
  
  // 状态机 - 添加内存访问状态
  val sFetch :: sExecute :: sMemRead :: sMemWrite :: sPush :: sPop :: sHalt :: Nil = Enum(7)
  val state = RegInit(sFetch)
  
  val instruction = RegInit(0.U(16.W))
  val memData = RegInit(0.U(16.W))  // 临时存储内存数据
  val returnAddr = RegInit(0.U(16.W))  // CALL/RET 用
  
  // 标志位提取
  def getCF = flags(0)
  def getZF = flags(6)
  def getSF = flags(7)
  def getOF = flags(11)
  def getPF = flags(2)
  
  // 辅助函数：更新标志位
  def updateFlags(result: UInt, carryOut: Bool, overflow: Bool): UInt = {
    val zf = result === 0.U
    val sf = result(15)
    val pf = PopCount(result(7, 0)) % 2.U === 0.U
    
    Cat(
      0.U(4.W),
      overflow,
      0.U(4.W),
      sf,
      zf,
      0.U(1.W),
      0.U(1.W),
      0.U(1.W),
      pf,
      0.U(1.W),
      carryOut
    )
  }
  
  // 寄存器选择器
  def selectReg(regCode: UInt): UInt = {
    MuxLookup(regCode, ax)(Seq(
      0.U -> ax,
      1.U -> cx,
      2.U -> dx,
      3.U -> bx,
      4.U -> sp,
      5.U -> bp,
      6.U -> si,
      7.U -> di
    ))
  }
  
  def updateReg(regCode: UInt, value: UInt): Unit = {
    switch(regCode) {
      is(0.U) { ax := value }
      is(1.U) { cx := value }
      is(2.U) { dx := value }
      is(3.U) { bx := value }
      is(4.U) { sp := value }
      is(5.U) { bp := value }
      is(6.U) { si := value }
      is(7.U) { di := value }
    }
  }
  
  // 条件检查函数
  def checkCondition(condCode: UInt): Bool = {
    MuxLookup(condCode, false.B)(Seq(
      0x4.U -> (getZF === true.B),              // JE/JZ
      0x5.U -> (getZF === false.B),             // JNE/JNZ
      0xF.U -> (getZF === false.B && getSF === getOF),  // JG/JNLE
      0xC.U -> (getSF =/= getOF),               // JL/JNGE
      0xD.U -> (getSF === getOF),               // JGE/JNL
      0xE.U -> (getZF === true.B || getSF =/= getOF),   // JLE/JNG
      0x7.U -> (getCF === false.B && getZF === false.B), // JA/JNBE
      0x3.U -> (getCF === false.B),             // JAE/JNB
      0x2.U -> (getCF === true.B),              // JB/JNAE
      0x6.U -> (getCF === true.B || getZF === true.B)   // JBE/JNA
    ))
  }
  
  // 默认输出
  io.memAddr := (cs << 4) + ip
  io.memDataOut := 0.U
  io.memWrite := false.B
  io.memRead := false.B
  io.halt := false.B
  io.ax := ax
  io.bx := bx
  io.cx := cx
  io.dx := dx
  io.sp := sp
  io.ip := ip
  io.flags := flags
  
  switch(state) {
    is(sFetch) {
      io.memAddr := (cs << 4) + ip
      io.memRead := true.B
      instruction := io.memDataIn
      ip := ip + 2.U
      state := sExecute
    }
    
    is(sExecute) {
      val opcode = instruction(15, 8)
      val operand = instruction(7, 0)
      val srcReg = operand(2, 0)
      val dstReg = operand(5, 3)
      
      switch(opcode) {
        // ===== 数据传送指令 =====
        
        // MOV reg, reg (0x89)
        is(0x89.U) {
          val srcVal = selectReg(srcReg)
          updateReg(dstReg, srcVal)
          state := sFetch
        }
        
        // MOV AX, imm16 (0xB8)
        is(0xB8.U) {
          ax := operand ## 0.U(8.W)
          state := sFetch
        }
        
        // MOV BX, imm16 (0xBB)
        is(0xBB.U) {
          bx := operand ## 0.U(8.W)
          state := sFetch
        }
        
        // MOV CX, imm16 (0xB9)
        is(0xB9.U) {
          cx := operand ## 0.U(8.W)
          state := sFetch
        }
        
        // MOV DX, imm16 (0xBA)
        is(0xBA.U) {
          dx := operand ## 0.U(8.W)
          state := sFetch
        }
        
        // ===== 算术运算指令 =====
        
        // ADD reg, reg (0x01)
        is(0x01.U) {
          val src = selectReg(srcReg)
          val dst = selectReg(dstReg)
          val result = dst +& src
          val carryOut = result(16)
          val overflow = (dst(15) === src(15)) && (dst(15) =/= result(15))
          
          updateReg(dstReg, result(15, 0))
          flags := updateFlags(result(15, 0), carryOut, overflow)
          state := sFetch
        }
        
        // SUB reg, reg (0x29)
        is(0x29.U) {
          val src = selectReg(srcReg)
          val dst = selectReg(dstReg)
          val result = dst -& src
          val carryOut = dst < src
          val overflow = (dst(15) =/= src(15)) && (dst(15) =/= result(15))
          
          updateReg(dstReg, result(15, 0))
          flags := updateFlags(result(15, 0), carryOut, overflow)
          state := sFetch
        }
        
        // INC reg (0x40-0x47)
        is(0x40.U) { ax := ax + 1.U; flags := updateFlags(ax + 1.U, getCF, false.B); state := sFetch }
        is(0x41.U) { cx := cx + 1.U; flags := updateFlags(cx + 1.U, getCF, false.B); state := sFetch }
        is(0x42.U) { dx := dx + 1.U; flags := updateFlags(dx + 1.U, getCF, false.B); state := sFetch }
        is(0x43.U) { bx := bx + 1.U; flags := updateFlags(bx + 1.U, getCF, false.B); state := sFetch }
        is(0x44.U) { sp := sp + 1.U; flags := updateFlags(sp + 1.U, getCF, false.B); state := sFetch }
        is(0x45.U) { bp := bp + 1.U; flags := updateFlags(bp + 1.U, getCF, false.B); state := sFetch }
        is(0x46.U) { si := si + 1.U; flags := updateFlags(si + 1.U, getCF, false.B); state := sFetch }
        is(0x47.U) { di := di + 1.U; flags := updateFlags(di + 1.U, getCF, false.B); state := sFetch }
        
        // DEC reg (0x48-0x4F)
        is(0x48.U) { ax := ax - 1.U; flags := updateFlags(ax - 1.U, getCF, false.B); state := sFetch }
        is(0x49.U) { cx := cx - 1.U; flags := updateFlags(cx - 1.U, getCF, false.B); state := sFetch }
        is(0x4A.U) { dx := dx - 1.U; flags := updateFlags(dx - 1.U, getCF, false.B); state := sFetch }
        is(0x4B.U) { bx := bx - 1.U; flags := updateFlags(bx - 1.U, getCF, false.B); state := sFetch }
        is(0x4C.U) { sp := sp - 1.U; flags := updateFlags(sp - 1.U, getCF, false.B); state := sFetch }
        is(0x4D.U) { bp := bp - 1.U; flags := updateFlags(bp - 1.U, getCF, false.B); state := sFetch }
        is(0x4E.U) { si := si - 1.U; flags := updateFlags(si - 1.U, getCF, false.B); state := sFetch }
        is(0x4F.U) { di := di - 1.U; flags := updateFlags(di - 1.U, getCF, false.B); state := sFetch }
        
        // CMP reg, reg (0x39)
        is(0x39.U) {
          val src = selectReg(srcReg)
          val dst = selectReg(dstReg)
          val result = dst -& src
          val carryOut = dst < src
          val overflow = (dst(15) =/= src(15)) && (dst(15) =/= result(15))
          
          flags := updateFlags(result(15, 0), carryOut, overflow)
          state := sFetch
        }
        
        // ===== 逻辑运算指令 =====
        
        // AND reg, reg (0x21)
        is(0x21.U) {
          val src = selectReg(srcReg)
          val dst = selectReg(dstReg)
          val result = dst & src
          
          updateReg(dstReg, result)
          flags := updateFlags(result, false.B, false.B)
          state := sFetch
        }
        
        // OR reg, reg (0x09)
        is(0x09.U) {
          val src = selectReg(srcReg)
          val dst = selectReg(dstReg)
          val result = dst | src
          
          updateReg(dstReg, result)
          flags := updateFlags(result, false.B, false.B)
          state := sFetch
        }
        
        // XOR reg, reg (0x31)
        is(0x31.U) {
          val src = selectReg(srcReg)
          val dst = selectReg(dstReg)
          val result = dst ^ src
          
          updateReg(dstReg, result)
          flags := updateFlags(result, false.B, false.B)
          state := sFetch
        }
        
        // NOT reg (0xF7/2 - 简化为 0xF7)
        is(0xF7.U) {
          val reg = operand(2, 0)
          val value = selectReg(reg)
          val result = ~value
          
          updateReg(reg, result)
          state := sFetch
        }
        
        // TEST reg, reg (0x85)
        is(0x85.U) {
          val src = selectReg(srcReg)
          val dst = selectReg(dstReg)
          val result = dst & src
          
          flags := updateFlags(result, false.B, false.B)
          state := sFetch
        }
        
        // ===== 栈操作指令 =====
        
        // PUSH AX (0x50)
        is(0x50.U) {
          sp := sp - 2.U
          memData := ax
          state := sPush
        }
        
        // PUSH CX (0x51)
        is(0x51.U) {
          sp := sp - 2.U
          memData := cx
          state := sPush
        }
        
        // PUSH DX (0x52)
        is(0x52.U) {
          sp := sp - 2.U
          memData := dx
          state := sPush
        }
        
        // PUSH BX (0x53)
        is(0x53.U) {
          sp := sp - 2.U
          memData := bx
          state := sPush
        }
        
        // POP AX (0x58)
        is(0x58.U) {
          state := sPop
        }
        
        // POP CX (0x59)
        is(0x59.U) {
          state := sPop
        }
        
        // POP DX (0x5A)
        is(0x5A.U) {
          state := sPop
        }
        
        // POP BX (0x5B)
        is(0x5B.U) {
          state := sPop
        }
        
        // ===== 控制转移指令 =====
        
        // JMP rel8 (0xEB)
        is(0xEB.U) {
          val offset = Cat(Fill(8, operand(7)), operand).asSInt
          ip := (ip.asSInt + offset).asUInt
          state := sFetch
        }
        
        // 条件跳转 (0x74-0x7F)
        is(0x74.U) { // JE/JZ
          when(checkCondition(0x4.U)) {
            val offset = Cat(Fill(8, operand(7)), operand).asSInt
            ip := (ip.asSInt + offset).asUInt
          }
          state := sFetch
        }
        
        is(0x75.U) { // JNE/JNZ
          when(checkCondition(0x5.U)) {
            val offset = Cat(Fill(8, operand(7)), operand).asSInt
            ip := (ip.asSInt + offset).asUInt
          }
          state := sFetch
        }
        
        is(0x7F.U) { // JG/JNLE
          when(checkCondition(0xF.U)) {
            val offset = Cat(Fill(8, operand(7)), operand).asSInt
            ip := (ip.asSInt + offset).asUInt
          }
          state := sFetch
        }
        
        is(0x7C.U) { // JL/JNGE
          when(checkCondition(0xC.U)) {
            val offset = Cat(Fill(8, operand(7)), operand).asSInt
            ip := (ip.asSInt + offset).asUInt
          }
          state := sFetch
        }
        
        is(0x7D.U) { // JGE/JNL
          when(checkCondition(0xD.U)) {
            val offset = Cat(Fill(8, operand(7)), operand).asSInt
            ip := (ip.asSInt + offset).asUInt
          }
          state := sFetch
        }
        
        is(0x7E.U) { // JLE/JNG
          when(checkCondition(0xE.U)) {
            val offset = Cat(Fill(8, operand(7)), operand).asSInt
            ip := (ip.asSInt + offset).asUInt
          }
          state := sFetch
        }
        
        // CALL near (0xE8 - 简化版)
        is(0xE8.U) {
          returnAddr := ip
          sp := sp - 2.U
          memData := ip
          val offset = Cat(Fill(8, operand(7)), operand).asSInt
          ip := (ip.asSInt + offset).asUInt
          state := sPush
        }
        
        // RET (0xC3)
        is(0xC3.U) {
          state := sPop
        }
        
        // ===== 处理器控制指令 =====
        
        // NOP (0x90)
        is(0x90.U) {
          state := sFetch
        }
        
        // HLT (0xF4)
        is(0xF4.U) {
          state := sHalt
        }
      }
      
      when(opcode =/= 0xF4.U && state === sExecute) {
        state := sFetch
      }
    }
    
    is(sPush) {
      io.memAddr := (ss << 4) + sp
      io.memDataOut := memData
      io.memWrite := true.B
      state := sFetch
    }
    
    is(sPop) {
      io.memAddr := (ss << 4) + sp
      io.memRead := true.B
      val popData = io.memDataIn
      
      // 根据之前的指令决定目标寄存器
      val prevOpcode = instruction(15, 8)
      switch(prevOpcode) {
        is(0x58.U) { ax := popData }
        is(0x59.U) { cx := popData }
        is(0x5A.U) { dx := popData }
        is(0x5B.U) { bx := popData }
        is(0xC3.U) { ip := popData }  // RET
      }
      
      sp := sp + 2.U
      state := sFetch
    }
    
    is(sHalt) {
      io.halt := true.B
    }
  }
}

// 顶层模块：CPU + Memory (M2 版本)
class MyCPU8086System_M2 extends Module {
  val io = IO(new Bundle {
    val halt = Output(Bool())
    val ax = Output(UInt(16.W))
    val bx = Output(UInt(16.W))
    val cx = Output(UInt(16.W))
    val dx = Output(UInt(16.W))
    val sp = Output(UInt(16.W))
    val ip = Output(UInt(16.W))
    val flags = Output(UInt(16.W))
  })
  
  val cpu = Module(new MyCPU8086_M2)
  val mem = Module(new Memory)
  
  // 连接 CPU 和内存
  mem.io.addr := cpu.io.memAddr
  mem.io.dataIn := cpu.io.memDataOut
  cpu.io.memDataIn := mem.io.dataOut
  mem.io.write := cpu.io.memWrite
  mem.io.read := cpu.io.memRead
  
  io.halt := cpu.io.halt
  io.ax := cpu.io.ax
  io.bx := cpu.io.bx
  io.cx := cpu.io.cx
  io.dx := cpu.io.dx
  io.sp := cpu.io.sp
  io.ip := cpu.io.ip
  io.flags := cpu.io.flags
}
