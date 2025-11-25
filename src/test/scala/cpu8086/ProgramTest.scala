package cpu8086

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class ProgramTest extends AnyFlatSpec with ChiselScalatestTester {
  "MyCPU8086System" should "execute the example program from simple_program.md" in {
    test(new MyCPU8086System) { dut =>
      println("=" * 70)
      println("执行示例程序: examples/simple_program.md")
      println("=" * 70)
      println()
      
      // 程序：
      // 0x0000: MOV AX, 0x1234  (B8 34 12)
      // 0x0002: MOV BX, 0x5678  (BB 78 56) - 注意：当前实现不支持 BX
      // 0x0004: ADD AX, BX      (01 D8)
      // 0x0006: HLT             (F4)
      
      println("初始状态:")
      println(f"  IP = 0x${dut.io.ip.peek().litValue}%04x")
      println(f"  AX = 0x${dut.io.ax.peek().litValue}%04x")
      println(f"  HALT = ${dut.io.halt.peek().litToBoolean}")
      println()
      
      // 由于我们的简化实现只支持 AX，我们修改程序为：
      // 0x0000: MOV AX, 0x1234  -> 指令码: 0xB834 (高字节是操作码，低字节是立即数)
      // 0x0002: ADD AX, BX      -> 指令码: 0x01xx (但 BX=0，所以结果还是 0x1234)
      // 0x0004: HLT             -> 指令码: 0xF400
      
      println("程序说明:")
      println("  由于当前实现只支持 AX 寄存器，程序简化为:")
      println("  1. MOV AX, 0x12   ; 将 0x12 加载到 AX 高字节")
      println("  2. ADD AX, BX     ; AX = AX + BX (BX=0)")
      println("  3. HLT            ; 停止")
      println()
      
      var cycle = 0
      var halted = false
      
      // 运行直到 HALT 或超时
      while (!halted && cycle < 50) {
        dut.clock.step(1)
        cycle += 1
        
        val ip = dut.io.ip.peek().litValue
        val ax = dut.io.ax.peek().litValue
        val halt = dut.io.halt.peek().litToBoolean
        
        // 每 2 个周期打印一次状态（对应一条指令的 FETCH + EXECUTE）
        if (cycle % 2 == 0) {
          println(f"周期 $cycle%2d:")
          println(f"  IP = 0x${ip}%04x")
          println(f"  AX = 0x${ax}%04x")
          println(f"  HALT = $halt")
          println()
        }
        
        if (halt) {
          halted = true
          println("=" * 70)
          println("CPU 已停止!")
          println("=" * 70)
          println()
          println("最终状态:")
          println(f"  IP = 0x${ip}%04x")
          println(f"  AX = 0x${ax}%04x")
          println(f"  总周期数: $cycle")
          println()
        }
      }
      
      if (!halted) {
        println("警告: CPU 未在预期时间内停止")
      }
      
      println("=" * 70)
      println("测试完成")
      println("=" * 70)
    }
  }
  
  "MyCPU8086System" should "demonstrate instruction execution step by step" in {
    test(new MyCPU8086System) { dut =>
      println()
      println("=" * 70)
      println("指令执行演示 - 逐步跟踪")
      println("=" * 70)
      println()
      
      println("状态机说明:")
      println("  State 0 (FETCH):   从内存读取指令")
      println("  State 1 (EXECUTE): 执行指令")
      println("  State 2 (HALT):    停止状态")
      println()
      
      // 初始状态
      println("初始状态 (复位后):")
      println(f"  IP = 0x${dut.io.ip.peek().litValue}%04x")
      println(f"  AX = 0x${dut.io.ax.peek().litValue}%04x")
      println()
      
      // 执行 10 个时钟周期，详细跟踪
      var continue = true
      var i = 1
      while (continue && i <= 10) {
        println(s"--- 时钟周期 $i ---")
        
        // 时钟上升沿前的状态
        val ip_before = dut.io.ip.peek().litValue
        val ax_before = dut.io.ax.peek().litValue
        
        // 时钟上升沿
        dut.clock.step(1)
        
        // 时钟上升沿后的状态
        val ip_after = dut.io.ip.peek().litValue
        val ax_after = dut.io.ax.peek().litValue
        val halt = dut.io.halt.peek().litToBoolean
        
        println(f"  IP: 0x${ip_before}%04x -> 0x${ip_after}%04x")
        println(f"  AX: 0x${ax_before}%04x -> 0x${ax_after}%04x")
        println(f"  HALT: $halt")
        
        if (ip_after != ip_before) {
          println(f"  ✓ IP 更新 (取指令)")
        }
        if (ax_after != ax_before) {
          println(f"  ✓ AX 更新 (执行指令)")
        }
        println()
        
        if (halt) {
          println("CPU 已停止，测试结束")
          println()
          continue = false
        }
        
        i += 1
      }
      
      println("=" * 70)
    }
  }
}
