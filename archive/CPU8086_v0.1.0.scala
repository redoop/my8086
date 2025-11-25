package cpu8086

import chisel3._
import chisel3.util._

// 简化的 8086 CPU 实现
class MyCPU8086 extends Module {
  val io = IO(new Bundle {
    val memAddr = Output(UInt(20.W))  // 20位地址总线
    val memDataOut = Output(UInt(16.W))
    val memDataIn = Input(UInt(16.W))
    val memWrite = Output(Bool())
    val memRead = Output(Bool())
    val halt = Output(Bool())
    val ax = Output(UInt(16.W))  // 调试输出
    val ip = Output(UInt(16.W))  // 调试输出
  })

  // 寄存器定义
  val ax = RegInit(0.U(16.W))  // 累加器
  val bx = RegInit(0.U(16.W))  // 基址寄存器
  val cx = RegInit(0.U(16.W))  // 计数器
  val dx = RegInit(0.U(16.W))  // 数据寄存器
  val sp = RegInit(0xFFFE.U(16.W))  // 栈指针
  val bp = RegInit(0.U(16.W))  // 基址指针
  val si = RegInit(0.U(16.W))  // 源变址
  val di = RegInit(0.U(16.W))  // 目的变址
  
  // 段寄存器
  val cs = RegInit(0xFFFF.U(16.W))  // 代码段
  val ds = RegInit(0.U(16.W))  // 数据段
  val ss = RegInit(0.U(16.W))  // 栈段
  val es = RegInit(0.U(16.W))  // 附加段
  
  // 指令指针和标志寄存器
  val ip = RegInit(0.U(16.W))
  val flags = RegInit(0.U(16.W))  // CF, PF, AF, ZF, SF, OF等
  
  // 状态机
  val sFetch :: sExecute :: sHalt :: Nil = Enum(3)
  val state = RegInit(sFetch)
  
  val instruction = RegInit(0.U(16.W))
  
  // 默认输出
  io.memAddr := (cs << 4) + ip
  io.memDataOut := 0.U
  io.memWrite := false.B
  io.memRead := false.B
  io.halt := false.B
  io.ax := ax
  io.ip := ip
  
  switch(state) {
    is(sFetch) {
      // 取指令
      io.memAddr := (cs << 4) + ip
      io.memRead := true.B
      instruction := io.memDataIn
      ip := ip + 2.U
      state := sExecute
    }
    
    is(sExecute) {
      // 简化的指令解码和执行
      val opcode = instruction(15, 8)
      
      switch(opcode) {
        // MOV AX, immediate
        is(0xB8.U) {
          ax := instruction(7, 0) ## 0.U(8.W)
          state := sFetch
        }
        
        // ADD AX, BX
        is(0x01.U) {
          val result = ax + bx
          ax := result
          // 更新标志位
          flags := Cat(result === 0.U, flags(14, 0))
          state := sFetch
        }
        
        // HLT
        is(0xF4.U) {
          state := sHalt
        }
      }
      
      when(opcode =/= 0xF4.U) {
        state := sFetch
      }
    }
    
    is(sHalt) {
      io.halt := true.B
    }
  }
}

// 简单的内存模块
class Memory extends Module {
  val io = IO(new Bundle {
    val addr = Input(UInt(20.W))
    val dataIn = Input(UInt(16.W))
    val dataOut = Output(UInt(16.W))
    val write = Input(Bool())
    val read = Input(Bool())
  })
  
  val mem = SyncReadMem(1024, UInt(16.W))
  
  io.dataOut := 0.U
  
  when(io.write) {
    mem.write(io.addr(9, 0), io.dataIn)
  }
  
  when(io.read) {
    io.dataOut := mem.read(io.addr(9, 0))
  }
}

// 顶层模块：CPU + Memory
class MyCPU8086System extends Module {
  val io = IO(new Bundle {
    val halt = Output(Bool())
    val ax = Output(UInt(16.W))
    val ip = Output(UInt(16.W))
  })
  
  val cpu = Module(new MyCPU8086)
  val mem = Module(new Memory)
  
  // 连接 CPU 和内存
  mem.io.addr := cpu.io.memAddr
  mem.io.dataIn := cpu.io.memDataOut
  cpu.io.memDataIn := mem.io.dataOut
  mem.io.write := cpu.io.memWrite
  mem.io.read := cpu.io.memRead
  
  io.halt := cpu.io.halt
  io.ax := cpu.io.ax
  io.ip := cpu.io.ip
}
