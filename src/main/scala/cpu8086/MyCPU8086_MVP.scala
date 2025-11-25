package cpu8086

import chisel3._
import chisel3.util._

/**
 * MyCPU8086 MVP (Minimum Viable Product)
 * Milestone 1: 实现最小可用系统
 * 
 * 新增指令:
 * - MOV reg, reg (AX, BX, CX, DX 之间)
 * - ADD reg, reg
 * - SUB reg, reg  
 * - INC reg
 * - DEC reg
 * - JMP rel8 (短跳转)
 * - CMP reg, reg
 */
class MyCPU8086_MVP extends Module {
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
  
  // 标志位定义 (按 8086 标准)
  // Bit 0: CF (Carry Flag)
  // Bit 2: PF (Parity Flag)
  // Bit 4: AF (Auxiliary Carry Flag)
  // Bit 6: ZF (Zero Flag)
  // Bit 7: SF (Sign Flag)
  // Bit 11: OF (Overflow Flag)
  val flags = RegInit(0.U(16.W))
  
  // 状态机
  val sFetch :: sExecute :: sHalt :: Nil = Enum(3)
  val state = RegInit(sFetch)
  
  val instruction = RegInit(0.U(16.W))
  
  // 辅助函数：更新标志位
  def updateFlags(result: UInt, carryOut: Bool, overflow: Bool): UInt = {
    val zf = result === 0.U
    val sf = result(15)
    val pf = PopCount(result(7, 0)) % 2.U === 0.U
    
    Cat(
      0.U(4.W),           // Bits 15-12: Reserved
      overflow,           // Bit 11: OF
      0.U(4.W),           // Bits 10-8: Reserved
      sf,                 // Bit 7: SF
      zf,                 // Bit 6: ZF
      0.U(1.W),           // Bit 5: Reserved
      0.U(1.W),           // Bit 4: AF (simplified)
      0.U(1.W),           // Bit 3: Reserved
      pf,                 // Bit 2: PF
      0.U(1.W),           // Bit 1: Reserved
      carryOut            // Bit 0: CF
    )
  }
  
  // 寄存器选择器
  def selectReg(regCode: UInt): UInt = {
    MuxLookup(regCode, ax)(Seq(
      0.U -> ax,
      1.U -> cx,
      2.U -> dx,
      3.U -> bx
    ))
  }
  
  def updateReg(regCode: UInt, value: UInt): Unit = {
    switch(regCode) {
      is(0.U) { ax := value }
      is(1.U) { cx := value }
      is(2.U) { dx := value }
      is(3.U) { bx := value }
    }
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
      val srcReg = operand(1, 0)
      val dstReg = operand(3, 2)
      
      switch(opcode) {
        // ===== 数据传送指令 =====
        
        // MOV reg, reg (0x89: MOV r/m, r)
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
        
        // INC AX (0x40)
        is(0x40.U) {
          val result = ax + 1.U
          ax := result
          flags := updateFlags(result, flags(0), false.B)
          state := sFetch
        }
        
        // INC CX (0x41)
        is(0x41.U) {
          val result = cx + 1.U
          cx := result
          flags := updateFlags(result, flags(0), false.B)
          state := sFetch
        }
        
        // INC DX (0x42)
        is(0x42.U) {
          val result = dx + 1.U
          dx := result
          flags := updateFlags(result, flags(0), false.B)
          state := sFetch
        }
        
        // INC BX (0x43)
        is(0x43.U) {
          val result = bx + 1.U
          bx := result
          flags := updateFlags(result, flags(0), false.B)
          state := sFetch
        }
        
        // DEC AX (0x48)
        is(0x48.U) {
          val result = ax - 1.U
          ax := result
          flags := updateFlags(result, flags(0), false.B)
          state := sFetch
        }
        
        // DEC CX (0x49)
        is(0x49.U) {
          val result = cx - 1.U
          cx := result
          flags := updateFlags(result, flags(0), false.B)
          state := sFetch
        }
        
        // DEC DX (0x4A)
        is(0x4A.U) {
          val result = dx - 1.U
          dx := result
          flags := updateFlags(result, flags(0), false.B)
          state := sFetch
        }
        
        // DEC BX (0x4B)
        is(0x4B.U) {
          val result = bx - 1.U
          bx := result
          flags := updateFlags(result, flags(0), false.B)
          state := sFetch
        }
        
        // CMP reg, reg (0x39)
        is(0x39.U) {
          val src = selectReg(srcReg)
          val dst = selectReg(dstReg)
          val result = dst -& src
          val carryOut = dst < src
          val overflow = (dst(15) =/= src(15)) && (dst(15) =/= result(15))
          
          // CMP 不更新寄存器，只更新标志位
          flags := updateFlags(result(15, 0), carryOut, overflow)
          state := sFetch
        }
        
        // ===== 控制转移指令 =====
        
        // JMP rel8 (0xEB: 短跳转)
        is(0xEB.U) {
          // 8位有符号偏移
          val offset = Cat(Fill(8, operand(7)), operand).asSInt
          ip := (ip.asSInt + offset).asUInt
          state := sFetch
        }
        
        // ===== 处理器控制指令 =====
        
        // HLT (0xF4)
        is(0xF4.U) {
          state := sHalt
        }
      }
      
      // 如果不是 HLT，继续执行
      when(opcode =/= 0xF4.U) {
        state := sFetch
      }
    }
    
    is(sHalt) {
      io.halt := true.B
    }
  }
}

// 顶层模块：CPU + Memory (MVP 版本)
class MyCPU8086System_MVP extends Module {
  val io = IO(new Bundle {
    val halt = Output(Bool())
    val ax = Output(UInt(16.W))
    val bx = Output(UInt(16.W))
    val cx = Output(UInt(16.W))
    val dx = Output(UInt(16.W))
    val ip = Output(UInt(16.W))
    val flags = Output(UInt(16.W))
  })
  
  val cpu = Module(new MyCPU8086_MVP)
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
  io.ip := cpu.io.ip
  io.flags := cpu.io.flags
}
