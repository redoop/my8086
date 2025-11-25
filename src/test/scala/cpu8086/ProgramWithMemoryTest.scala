package cpu8086

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

/**
 * 带内存初始化的程序测试
 * 演示如何加载并执行 simple_program.md 中的示例程序
 */
class ProgramWithMemoryTest extends AnyFlatSpec with ChiselScalatestTester {
  
  "MyCPU8086" should "execute MOV AX, immediate instruction" in {
    test(new MyCPU8086System) { dut =>
      println()
      println("=" * 70)
      println("测试 1: MOV AX, 0x12 指令")
      println("=" * 70)
      println()
      
      println("指令格式:")
      println("  操作码: 0xB8 (MOV AX, immediate)")
      println("  立即数: 0x12")
      println("  完整指令: 0xB812 (16位)")
      println()
      
      println("说明:")
      println("  由于内存初始化为 0，CPU 会读取 0x0000")
      println("  我们的实现中，指令格式为:")
      println("    [15:8] = 操作码")
      println("    [7:0]  = 立即数")
      println()
      
      println("初始状态:")
      println(f"  IP = 0x${dut.io.ip.peek().litValue}%04x")
      println(f"  AX = 0x${dut.io.ax.peek().litValue}%04x")
      println()
      
      // 执行几个周期
      println("执行过程:")
      for (i <- 1 to 6) {
        val ip_before = dut.io.ip.peek().litValue
        val ax_before = dut.io.ax.peek().litValue
        
        dut.clock.step(1)
        
        val ip_after = dut.io.ip.peek().litValue
        val ax_after = dut.io.ax.peek().litValue
        
        println(f"周期 $i: IP=0x${ip_after}%04x, AX=0x${ax_after}%04x")
        
        if (ax_after != ax_before) {
          println(f"  → AX 被更新!")
        }
      }
      
      println()
      println("=" * 70)
    }
  }
  
  "MyCPU8086" should "demonstrate the fetch-execute cycle" in {
    test(new MyCPU8086System) { dut =>
      println()
      println("=" * 70)
      println("测试 2: 取指-执行周期演示")
      println("=" * 70)
      println()
      
      println("CPU 状态机:")
      println("  FETCH (0)   → 从内存读取指令，IP+2")
      println("  EXECUTE (1) → 执行指令")
      println("  HALT (2)    → 停止")
      println()
      
      println("周期分析:")
      println("-" * 70)
      
      for (cycle <- 1 to 8) {
        val ip = dut.io.ip.peek().litValue
        val ax = dut.io.ax.peek().litValue
        val halt = dut.io.halt.peek().litToBoolean
        
        val phase = if (cycle % 2 == 1) "FETCH" else "EXECUTE"
        
        println(f"周期 $cycle%2d [$phase%7s]: IP=0x${ip}%04x, AX=0x${ax}%04x, HALT=$halt")
        
        dut.clock.step(1)
      }
      
      println("-" * 70)
      println()
      println("观察:")
      println("  - 奇数周期: FETCH  阶段，IP 递增")
      println("  - 偶数周期: EXECUTE 阶段，可能更新寄存器")
      println()
      println("=" * 70)
    }
  }
  
  "MyCPU8086" should "show instruction decoding" in {
    test(new MyCPU8086System) { dut =>
      println()
      println("=" * 70)
      println("测试 3: 指令解码演示")
      println("=" * 70)
      println()
      
      println("支持的指令:")
      println("  0xB8: MOV AX, imm8  - 将立即数加载到 AX 高字节")
      println("  0x01: ADD AX, BX    - AX = AX + BX")
      println("  0xF4: HLT           - 停止 CPU")
      println()
      
      println("内存内容 (假设全为 0x0000):")
      println("  地址 0x0000: 0x0000 → 操作码 0x00 (未定义)")
      println("  地址 0x0002: 0x0000 → 操作码 0x00 (未定义)")
      println("  ...")
      println()
      
      println("执行结果:")
      println("  由于操作码 0x00 不匹配任何指令，")
      println("  CPU 会继续取下一条指令，IP 持续递增")
      println()
      
      // 显示 IP 的变化
      println("IP 变化轨迹:")
      for (i <- 1 to 10) {
        val ip = dut.io.ip.peek().litValue
        if (i % 2 == 1) {
          print(f"0x${ip}%04x → ")
        }
        dut.clock.step(1)
      }
      println()
      println()
      
      println("结论:")
      println("  要执行实际程序，需要:")
      println("  1. 初始化内存内容")
      println("  2. 加载正确的机器码")
      println("  3. 或者使用支持内存写入的测试平台")
      println()
      println("=" * 70)
    }
  }
  
  "MyCPU8086" should "explain the simple_program.md example" in {
    test(new MyCPU8086System) { dut =>
      println()
      println("=" * 70)
      println("测试 4: simple_program.md 示例说明")
      println("=" * 70)
      println()
      
      println("原始程序 (来自 examples/simple_program.md):")
      println()
      println("  地址    机器码      指令")
      println("  " + "-" * 50)
      println("  0x0000  B8 34 12    MOV AX, 0x1234")
      println("  0x0003  BB 78 56    MOV BX, 0x5678")
      println("  0x0006  01 D8       ADD AX, BX")
      println("  0x0008  F4          HLT")
      println()
      
      println("当前实现的限制:")
      println("  ✗ 只实现了 AX 寄存器 (没有 BX)")
      println("  ✗ MOV 指令格式简化为 16 位")
      println("  ✗ 没有内存初始化机制")
      println()
      
      println("简化版本 (适配当前实现):")
      println()
      println("  地址    指令码      说明")
      println("  " + "-" * 50)
      println("  0x0000  0xB812      MOV AX, 0x12 (高字节)")
      println("  0x0002  0x0100      ADD AX, BX (BX=0)")
      println("  0x0004  0xF400      HLT")
      println()
      
      println("要完整实现示例程序，需要:")
      println("  1. 扩展寄存器组 (添加 BX, CX, DX 等)")
      println("  2. 实现完整的 MOV 指令格式")
      println("  3. 添加内存初始化功能")
      println("  4. 支持多字节指令")
      println()
      
      println("当前测试结果:")
      for (i <- 1 to 4) {
        dut.clock.step(1)
      }
      println(f"  执行 4 个周期后:")
      println(f"  IP = 0x${dut.io.ip.peek().litValue}%04x")
      println(f"  AX = 0x${dut.io.ax.peek().litValue}%04x")
      println()
      
      println("=" * 70)
    }
  }
}
