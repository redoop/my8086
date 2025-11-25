package cpu8086

import chisel3._
import chisel3.util._

/**
 * Memory - 简单的内存模块
 * 
 * 1MB 地址空间 (20-bit 地址)
 * 16-bit 数据宽度
 */
class Memory extends Module {
  val io = IO(new Bundle {
    val addr = Input(UInt(20.W))
    val dataIn = Input(UInt(16.W))
    val dataOut = Output(UInt(16.W))
    val write = Input(Bool())
    val read = Input(Bool())
  })
  
  // 64KB 内存 (简化版，实际 8086 有 1MB)
  val mem = SyncReadMem(32768, UInt(16.W))
  
  // 默认输出
  io.dataOut := 0.U
  
  // 读操作
  when(io.read) {
    io.dataOut := mem.read(io.addr(15, 1))
  }
  
  // 写操作
  when(io.write) {
    mem.write(io.addr(15, 1), io.dataIn)
  }
}
