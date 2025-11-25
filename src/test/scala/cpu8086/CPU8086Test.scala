package cpu8086

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CPU8086Test extends AnyFlatSpec with ChiselScalatestTester {
  "CPU8086System" should "execute basic instructions" in {
    test(new CPU8086System) { dut =>
      println("=== 8086 CPU 模拟测试 ===")
      println("初始状态:")
      println(f"  AX = 0x${dut.io.ax.peek().litValue}%04x")
      println(f"  IP = 0x${dut.io.ip.peek().litValue}%04x")
      
      // 运行几个时钟周期
      for (i <- 0 until 20) {
        dut.clock.step(1)
        if (i % 5 == 0) {
          println(s"\n周期 $i:")
          println(f"  AX = 0x${dut.io.ax.peek().litValue}%04x")
          println(f"  IP = 0x${dut.io.ip.peek().litValue}%04x")
          println(s"  HALT = ${dut.io.halt.peek().litToBoolean}")
        }
        
        if (dut.io.halt.peek().litToBoolean) {
          println(s"\nCPU 已停止在周期 $i")
          println("最终状态:")
          println(f"  AX = 0x${dut.io.ax.peek().litValue}%04x")
          println(f"  IP = 0x${dut.io.ip.peek().litValue}%04x")
        }
      }
      
      println("\n=== 测试完成 ===")
    }
  }
}
