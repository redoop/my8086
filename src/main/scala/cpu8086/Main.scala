package cpu8086

import chisel3._

object Main {
  def main(args: Array[String]): Unit = {
    println("=== 生成 MyCPU8086 Verilog 代码 ===")
    
    // 生成 Verilog
    (new chisel3.stage.ChiselStage).emitVerilog(
      new MyCPU8086System,
      Array("--target-dir", "generated")
    )
    
    println("✓ Verilog 代码已生成到 generated/MyCPU8086System.v")
  }
}
