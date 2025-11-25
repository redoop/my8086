# My8086 - Chisel 8086 CPU 实现

这是一个使用 Chisel HDL 实现的简化版 8086 CPU。

## 功能特性

- 16位数据总线
- 20位地址总线
- 基本寄存器组（AX, BX, CX, DX, SP, BP, SI, DI）
- 段寄存器（CS, DS, SS, ES）
- 简化的指令集
- 内存接口

## 运行模拟

```bash
# 运行测试
sbt test

# 生成 Verilog 代码
sbt "runMain cpu8086.Main"
```

## 项目结构

```
.
├── build.sbt                          # SBT 构建配置
├── src/
│   ├── main/scala/cpu8086/
│   │   ├── CPU8086.scala             # CPU 实现
│   │   └── Main.scala                # Verilog 生成器
│   └── test/scala/cpu8086/
│       └── CPU8086Test.scala         # 测试用例
└── generated/                         # 生成的 Verilog 代码
```

## 实现的指令

- MOV AX, immediate
- ADD AX, BX
- HLT

## 寄存器

- **通用寄存器**: AX, BX, CX, DX
- **指针和索引**: SP, BP, SI, DI
- **段寄存器**: CS, DS, SS, ES
- **指令指针**: IP
- **标志寄存器**: FLAGS
