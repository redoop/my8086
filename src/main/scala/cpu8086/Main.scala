package cpu8086

import chisel3._
import chisel3.stage.ChiselStage

object Main extends App {
  println("=== 生成 8086 CPU Verilog 代码 ===")
  
  (new ChiselStage).emitVerilog(
    new CPU8086System,
    Array("--target-dir", "generated")
  )
  
  println("Verilog 代码已生成到 generated/ 目录")
}
