# MyCPU8086 测试文档

## 测试概述

本项目包含多个测试套件，用于验证 MyCPU8086 的功能和演示 CPU 的工作原理。

## 测试套件

### 1. MyCPU8086Test - 基础功能测试
**文件**: `src/test/scala/cpu8086/CPU8086Test.scala`

**测试内容**:
- 基本的指令执行
- 寄存器状态跟踪
- HALT 信号检测

**运行方式**:
```bash
sbt "testOnly cpu8086.MyCPU8086Test"
```

**输出示例**:
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

### 2. ProgramTest - 程序执行测试
**文件**: `src/test/scala/cpu8086/ProgramTest.scala`

**测试内容**:
- 执行 simple_program.md 中的示例程序
- 逐步跟踪指令执行
- 演示取指-执行周期

**运行方式**:
```bash
sbt "testOnly cpu8086.ProgramTest"
```

**测试用例**:
1. **execute the example program** - 执行完整程序
2. **demonstrate instruction execution step by step** - 逐步演示

### 3. ProgramWithMemoryTest - 详细程序分析
**文件**: `src/test/scala/cpu8086/ProgramWithMemoryTest.scala`

**测试内容**:
- MOV 指令测试
- 取指-执行周期演示
- 指令解码说明
- simple_program.md 示例分析

**运行方式**:
```bash
sbt "testOnly cpu8086.ProgramWithMemoryTest"
```

**测试用例**:
1. **execute MOV AX, immediate instruction** - MOV 指令测试
2. **demonstrate the fetch-execute cycle** - 周期演示
3. **show instruction decoding** - 指令解码
4. **explain the simple_program.md example** - 示例说明

## 运行所有测试

```bash
# 运行所有测试
sbt test

# 运行特定测试类
sbt "testOnly cpu8086.*"

# 运行特定测试方法
sbt "testOnly cpu8086.ProgramWithMemoryTest -- -z 'MOV'"
```

## 测试结果解读

### CPU 状态机

MyCPU8086 使用简单的两阶段状态机：

```
FETCH (State 0)
  ↓
  - 从内存读取指令
  - IP = IP + 2
  - 保存指令到 instruction 寄存器
  ↓
EXECUTE (State 1)
  ↓
  - 解码指令 (opcode = instruction[15:8])
  - 执行指令
  - 更新寄存器
  ↓
  如果是 HLT → HALT (State 2)
  否则 → FETCH
```

### 指令格式

当前实现的指令格式（16位）：

```
+--------+--------+
| [15:8] | [7:0]  |
+--------+--------+
| Opcode |  Data  |
+--------+--------+
```

支持的指令：
- `0xB8`: MOV AX, imm8 - 将立即数加载到 AX 高字节
- `0x01`: ADD AX, BX - AX = AX + BX (BX 当前为 0)
- `0xF4`: HLT - 停止 CPU

### 时序分析

每条指令需要 2 个时钟周期：

| 周期 | 阶段 | 操作 | IP 变化 | 寄存器变化 |
|------|------|------|---------|-----------|
| 1 | FETCH | 读取指令 | +2 | 无 |
| 2 | EXECUTE | 执行指令 | 无 | 可能更新 |

## 与 simple_program.md 的对应关系

### 原始程序（标准 8086）

```assembly
START:
    MOV AX, 0x1234    ; B8 34 12
    MOV BX, 0x5678    ; BB 78 56
    ADD AX, BX        ; 01 D8
    HLT               ; F4
```

### 当前实现的限制

| 特性 | 标准 8086 | MyCPU8086 | 状态 |
|------|-----------|-----------|------|
| 寄存器 | AX, BX, CX, DX, ... | 仅 AX | ✗ |
| MOV 格式 | 多字节 | 16位单字 | ✗ |
| ADD 操作 | 完整 ALU | 简化版 | ✗ |
| 内存初始化 | 支持 | 不支持 | ✗ |
| HLT 指令 | 支持 | 支持 | ✓ |

### 简化版本（适配当前实现）

```
地址    指令码      说明
0x0000  0xB812      MOV AX, 0x12 (高字节)
0x0002  0x0100      ADD AX, BX (BX=0)
0x0004  0xF400      HLT
```

## 测试覆盖率

### 已测试功能
- ✓ 基本状态机（FETCH/EXECUTE/HALT）
- ✓ IP 寄存器递增
- ✓ 指令读取
- ✓ 简单指令解码
- ✓ HALT 信号

### 未测试功能
- ✗ 实际的 MOV 指令执行（内存为空）
- ✗ ADD 指令执行（BX 未实现）
- ✗ 内存写入
- ✗ 标志位更新
- ✗ 其他寄存器

## 改进建议

要完整实现 simple_program.md 中的示例，需要：

### 1. 扩展寄存器组
```scala
val bx = RegInit(0.U(16.W))
val cx = RegInit(0.U(16.W))
val dx = RegInit(0.U(16.W))
```

### 2. 实现完整的 MOV 指令
```scala
// MOV AX, immediate (16-bit)
is(0xB8.U) {
  ax := io.memDataIn  // 读取完整的 16 位立即数
  state := sFetch
}

// MOV BX, immediate
is(0xBB.U) {
  bx := io.memDataIn
  state := sFetch
}
```

### 3. 实现 ADD 指令
```scala
// ADD AX, BX
is(0x01.U) {
  val result = ax + bx
  ax := result
  // 更新标志位
  flags := Cat(
    result === 0.U,      // ZF
    result(15),          // SF
    // ... 其他标志位
    flags(13, 0)
  )
  state := sFetch
}
```

### 4. 添加内存初始化
```scala
// 在测试中初始化内存
val program = Seq(
  0xB834.U,  // MOV AX, 0x34
  0x0100.U,  // ADD AX, BX
  0xF400.U   // HLT
)

// 写入内存（需要添加内存写入接口）
```

## 调试技巧

### 1. 查看波形
```scala
test(new MyCPU8086System)
  .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
  // 测试代码
}
```

### 2. 打印内部状态
在 Chisel 代码中添加：
```scala
printf(p"State: $state, IP: $ip, AX: $ax\n")
```

### 3. 单步执行
```scala
for (i <- 1 to 10) {
  println(s"Step $i")
  dut.clock.step(1)
  // 检查状态
}
```

## 参考资料

- [Chisel Testing Documentation](https://www.chisel-lang.org/chiseltest/)
- [Intel 8086 Instruction Set](https://edge.edx.org/c4x/BITSPilani/EEE231/asset/8086_family_Users_Manual_1_.pdf)
- [examples/simple_program.md](../examples/simple_program.md)
