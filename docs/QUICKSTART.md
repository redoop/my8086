# My8086 快速入门指南

## 项目概述

My8086 是一个使用 Chisel HDL 实现的简化版 Intel 8086 CPU。项目包含：
- CPU 核心实现
- 简单的内存模块
- 测试框架
- Verilog 代码生成

## 环境要求

- Java 11 或更高版本
- SBT (Scala Build Tool)
- Scala 2.13.10

## 快速开始

### 1. 运行模拟测试

```bash
sbt test
```

这将编译并运行 CPU 的功能测试，输出类似：

```
=== 8086 CPU 模拟测试 ===
初始状态:
  AX = 0x0000
  IP = 0x0000

周期 0:
  AX = 0x0000
  IP = 0x0002
  HALT = false
...
```

### 2. 生成 Verilog 代码

```bash
sbt "runMain cpu8086.Main"
```

生成的 Verilog 文件位于 `generated/CPU8086System.v`

### 3. 查看生成的 Verilog

```bash
cat generated/CPU8086System.v
```

## 项目结构

```
.
├── build.sbt                          # SBT 构建配置
├── README.md                          # 项目说明
├── QUICKSTART.md                      # 快速入门（本文件）
├── src/
│   ├── main/scala/cpu8086/
│   │   ├── CPU8086.scala             # CPU 核心实现
│   │   └── Main.scala                # Verilog 生成器
│   └── test/scala/cpu8086/
│       └── CPU8086Test.scala         # 测试用例
├── examples/
│   └── simple_program.md             # 示例程序说明
└── generated/
    └── CPU8086System.v               # 生成的 Verilog 代码
```

## CPU 特性

### 实现的寄存器
- 通用寄存器: AX, BX, CX, DX
- 段寄存器: CS, DS, SS, ES
- 指针寄存器: IP, SP, BP, SI, DI
- 标志寄存器: FLAGS

### 实现的指令（简化版）
- `MOV AX, immediate` (0xB8): 将立即数加载到 AX
- `ADD AX, BX` (0x01): AX = AX + BX
- `HLT` (0xF4): 停止 CPU

### 总线规格
- 20位地址总线（1MB 寻址空间）
- 16位数据总线

## 状态机

CPU 使用简单的两阶段状态机：

1. **FETCH**: 从内存取指令
2. **EXECUTE**: 执行指令
3. **HALT**: 停止状态

## 测试说明

测试程序会：
1. 初始化 CPU
2. 运行 20 个时钟周期
3. 每 5 个周期输出寄存器状态
4. 检测 HALT 信号

## 扩展建议

如果你想扩展这个 CPU，可以考虑：

1. **添加更多指令**
   - SUB, MUL, DIV
   - JMP, JZ, JNZ (跳转指令)
   - PUSH, POP (栈操作)
   - MOV 的更多寻址模式

2. **改进内存系统**
   - 增加内存容量
   - 实现内存映射 I/O
   - 添加缓存

3. **增强调试功能**
   - 添加断点支持
   - 实现单步执行
   - 生成波形文件（VCD）

4. **性能优化**
   - 实现流水线
   - 添加分支预测
   - 优化指令解码

## 常见问题

### Q: 如何添加新指令？

在 `CPU8086.scala` 的 `sExecute` 状态中添加新的 case：

```scala
// 例如添加 SUB 指令
is(0x29.U) {  // SUB opcode
  val result = ax - bx
  ax := result
  state := sFetch
}
```

### Q: 如何增加内存大小？

修改 `Memory` 模块中的内存定义：

```scala
val mem = SyncReadMem(4096, UInt(16.W))  // 从 1024 改为 4096
```

### Q: 如何生成波形文件？

在测试中使用 `WriteVcdAnnotation`：

```scala
test(new CPU8086System)
  .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
  // 测试代码
}
```

## 参考资料

- [Chisel 官方文档](https://www.chisel-lang.org/)
- [Intel 8086 手册](https://edge.edx.org/c4x/BITSPilani/EEE231/asset/8086_family_Users_Manual_1_.pdf)
- [Chisel Bootcamp](https://github.com/freechipsproject/chisel-bootcamp)

## 许可证

本项目仅用于教育和学习目的。
