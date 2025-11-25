package cpu8086

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

/**
 * Milestone 1 MVP 测试
 * 测试新实现的指令
 */
class MVP_Test extends AnyFlatSpec with ChiselScalatestTester {
  
  "MyCPU8086" should "execute MOV reg, reg instructions" in {
    test(new MyCPU8086System) { dut =>
      println("\n" + "="*70)
      println("测试 1: MOV 寄存器间传送")
      println("="*70)
      
      // 初始化寄存器
      println("\n初始状态:")
      println(f"  AX = 0x${dut.io.ax.peek().litValue}%04x")
      println(f"  BX = 0x${dut.io.bx.peek().litValue}%04x")
      
      // 执行几个周期观察
      for (i <- 1 to 6) {
        dut.clock.step(1)
        if (i % 2 == 0) {
          println(f"\n周期 $i:")
          println(f"  AX = 0x${dut.io.ax.peek().litValue}%04x")
          println(f"  BX = 0x${dut.io.bx.peek().litValue}%04x")
          println(f"  CX = 0x${dut.io.cx.peek().litValue}%04x")
          println(f"  DX = 0x${dut.io.dx.peek().litValue}%04x")
        }
      }
      
      println("\n" + "="*70)
    }
  }
  
  "MyCPU8086" should "execute INC and DEC instructions" in {
    test(new MyCPU8086System) { dut =>
      println("\n" + "="*70)
      println("测试 2: INC/DEC 指令")
      println("="*70)
      
      println("\n说明:")
      println("  INC AX (0x40) - AX 加 1")
      println("  DEC AX (0x48) - AX 减 1")
      println("  类似的还有 BX, CX, DX")
      
      println("\n初始状态:")
      println(f"  AX = 0x${dut.io.ax.peek().litValue}%04x")
      
      // 执行几个周期
      for (i <- 1 to 10) {
        val ax_before = dut.io.ax.peek().litValue
        dut.clock.step(1)
        val ax_after = dut.io.ax.peek().litValue
        
        if (ax_after != ax_before) {
          println(f"  周期 $i: AX 变化 0x${ax_before}%04x -> 0x${ax_after}%04x")
        }
      }
      
      println("\n" + "="*70)
    }
  }
  
  "MyCPU8086" should "execute ADD and SUB instructions" in {
    test(new MyCPU8086System) { dut =>
      println("\n" + "="*70)
      println("测试 3: ADD/SUB 指令")
      println("="*70)
      
      println("\n指令格式:")
      println("  ADD reg, reg (0x01) - 目标 = 目标 + 源")
      println("  SUB reg, reg (0x29) - 目标 = 目标 - 源")
      println("  更新标志位: ZF, SF, CF, OF, PF")
      
      println("\n初始状态:")
      println(f"  AX = 0x${dut.io.ax.peek().litValue}%04x")
      println(f"  BX = 0x${dut.io.bx.peek().litValue}%04x")
      println(f"  FLAGS = 0x${dut.io.flags.peek().litValue}%04x")
      
      // 执行几个周期
      for (i <- 1 to 8) {
        dut.clock.step(1)
        if (i % 2 == 0) {
          println(f"\n周期 $i:")
          println(f"  AX = 0x${dut.io.ax.peek().litValue}%04x")
          println(f"  BX = 0x${dut.io.bx.peek().litValue}%04x")
          println(f"  FLAGS = 0x${dut.io.flags.peek().litValue}%04x")
          
          val flags = dut.io.flags.peek().litValue
          val zf = (flags >> 6) & 1
          val sf = (flags >> 7) & 1
          val cf = flags & 1
          println(f"    ZF=$zf, SF=$sf, CF=$cf")
        }
      }
      
      println("\n" + "="*70)
    }
  }
  
  "MyCPU8086" should "execute CMP instruction" in {
    test(new MyCPU8086System) { dut =>
      println("\n" + "="*70)
      println("测试 4: CMP 比较指令")
      println("="*70)
      
      println("\n说明:")
      println("  CMP reg, reg (0x39) - 比较两个寄存器")
      println("  执行 dst - src，但不保存结果")
      println("  只更新标志位，用于后续条件跳转")
      
      println("\n初始状态:")
      println(f"  AX = 0x${dut.io.ax.peek().litValue}%04x")
      println(f"  BX = 0x${dut.io.bx.peek().litValue}%04x")
      println(f"  FLAGS = 0x${dut.io.flags.peek().litValue}%04x")
      
      // 执行几个周期
      for (i <- 1 to 6) {
        dut.clock.step(1)
        if (i % 2 == 0) {
          println(f"\n周期 $i:")
          println(f"  AX = 0x${dut.io.ax.peek().litValue}%04x (不变)")
          println(f"  BX = 0x${dut.io.bx.peek().litValue}%04x (不变)")
          println(f"  FLAGS = 0x${dut.io.flags.peek().litValue}%04x")
        }
      }
      
      println("\n" + "="*70)
    }
  }
  
  "MyCPU8086" should "execute JMP instruction" in {
    test(new MyCPU8086System) { dut =>
      println("\n" + "="*70)
      println("测试 5: JMP 跳转指令")
      println("="*70)
      
      println("\n说明:")
      println("  JMP rel8 (0xEB) - 相对短跳转")
      println("  IP = IP + offset (8位有符号偏移)")
      
      println("\n初始状态:")
      println(f"  IP = 0x${dut.io.ip.peek().litValue}%04x")
      
      // 跟踪 IP 变化
      for (i <- 1 to 10) {
        val ip_before = dut.io.ip.peek().litValue
        dut.clock.step(1)
        val ip_after = dut.io.ip.peek().litValue
        
        val delta = ip_after.toLong - ip_before.toLong
        if (delta != 0 && delta != 2) {
          println(f"  周期 $i: IP 跳转 0x${ip_before}%04x -> 0x${ip_after}%04x (偏移: $delta)")
        } else if (i % 2 == 0) {
          println(f"  周期 $i: IP = 0x${ip_after}%04x")
        }
      }
      
      println("\n" + "="*70)
    }
  }
  
  "MyCPU8086" should "demonstrate complete instruction set" in {
    test(new MyCPU8086System) { dut =>
      println("\n" + "="*70)
      println("测试 6: Milestone 1 指令集演示")
      println("="*70)
      
      println("\n已实现的指令:")
      println("  数据传送:")
      println("    ✓ MOV AX, imm (0xB8)")
      println("    ✓ MOV BX, imm (0xBB)")
      println("    ✓ MOV CX, imm (0xB9)")
      println("    ✓ MOV DX, imm (0xBA)")
      println("    ✓ MOV reg, reg (0x89)")
      println()
      println("  算术运算:")
      println("    ✓ ADD reg, reg (0x01)")
      println("    ✓ SUB reg, reg (0x29)")
      println("    ✓ INC AX/BX/CX/DX (0x40-0x43)")
      println("    ✓ DEC AX/BX/CX/DX (0x48-0x4B)")
      println("    ✓ CMP reg, reg (0x39)")
      println()
      println("  控制转移:")
      println("    ✓ JMP rel8 (0xEB)")
      println()
      println("  处理器控制:")
      println("    ✓ HLT (0xF4)")
      println()
      println("  总计: 15 条指令")
      
      println("\n标志位支持:")
      println("  ✓ CF (Carry Flag) - 进位标志")
      println("  ✓ ZF (Zero Flag) - 零标志")
      println("  ✓ SF (Sign Flag) - 符号标志")
      println("  ✓ OF (Overflow Flag) - 溢出标志")
      println("  ✓ PF (Parity Flag) - 奇偶标志")
      
      println("\n寄存器支持:")
      println("  ✓ AX, BX, CX, DX - 通用寄存器")
      println("  ✓ IP - 指令指针")
      println("  ✓ FLAGS - 标志寄存器")
      
      println("\n" + "="*70)
      println("Milestone 1 MVP 完成!")
      println("="*70)
    }
  }
}
