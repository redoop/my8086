# 更新日志

## [v0.3.0] - 2025-11-25

### 重构
- 🔄 **统一代码库**: 合并 MVP 和 M2 版本到单一实现
  - 删除 `MyCPU8086_MVP.scala` 和 `MyCPU8086_M2.scala`
  - 统一为 `CPU8086.scala` (MyCPU8086 类)
  - 所有旧版本已归档到 `archive/` 目录

### 新增
- ✨ **Memory 模块**: 添加独立的内存模块 (`Memory.scala`)
  - 64KB 同步读写内存
  - 16位数据宽度
  - 20位地址空间支持

### 改进
- 📝 更新所有测试用例以使用新的统一类名
- ✅ 所有 13 个测试用例通过
- 📦 成功生成 Verilog 代码 (130KB)

### 项目结构
```
src/main/scala/cpu8086/
├── CPU8086.scala    # 统一的 CPU 实现 (520 行)
├── Memory.scala     # 内存模块 (36 行)
└── Main.scala       # Verilog 生成器 (17 行)
```

---

## [v0.2.0] - Milestone 1 MVP

### 功能特性
- ✅ 15 条指令实现 (14.4% of Intel 8086)
- ✅ 完整寄存器组: AX, BX, CX, DX, SP, BP, SI, DI
- ✅ 完整标志位: CF, ZF, SF, OF, PF
- ✅ 基础算术运算: ADD, SUB, INC, DEC, CMP
- ✅ 数据传送: MOV reg/imm
- ✅ 控制转移: JMP
- ✅ 处理器控制: HLT

### 测试
- 6 个新测试用例
- 完整的功能验证

---

## [v0.1.0] - 初始版本

### 功能特性
- ✅ 3 条基础指令: MOV AX, ADD, HLT
- ✅ 单寄存器 (AX)
- ✅ 基础标志位 (ZF)
- ✅ 简单状态机

### 测试
- 7 个基础测试用例
- 概念验证
