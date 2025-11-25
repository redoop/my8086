module MyCPU8086(
  input         clock,
  input         reset,
  output [19:0] io_memAddr, // @[src/main/scala/cpu8086/CPU8086.scala 8:14]
  input  [15:0] io_memDataIn, // @[src/main/scala/cpu8086/CPU8086.scala 8:14]
  output        io_memRead, // @[src/main/scala/cpu8086/CPU8086.scala 8:14]
  output        io_halt, // @[src/main/scala/cpu8086/CPU8086.scala 8:14]
  output [15:0] io_ax, // @[src/main/scala/cpu8086/CPU8086.scala 8:14]
  output [15:0] io_ip // @[src/main/scala/cpu8086/CPU8086.scala 8:14]
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
`endif // RANDOMIZE_REG_INIT
  reg [15:0] ax; // @[src/main/scala/cpu8086/CPU8086.scala 20:19]
  reg [15:0] ip; // @[src/main/scala/cpu8086/CPU8086.scala 36:19]
  reg [1:0] state; // @[src/main/scala/cpu8086/CPU8086.scala 41:22]
  reg [15:0] instruction; // @[src/main/scala/cpu8086/CPU8086.scala 43:28]
  wire [19:0] _GEN_21 = {{4'd0}, ip}; // @[src/main/scala/cpu8086/CPU8086.scala 46:27]
  wire [19:0] _io_memAddr_T_2 = 20'hffff0 + _GEN_21; // @[src/main/scala/cpu8086/CPU8086.scala 46:27]
  wire [15:0] _ip_T_1 = ip + 16'h2; // @[src/main/scala/cpu8086/CPU8086.scala 60:16]
  wire [7:0] opcode = instruction[15:8]; // @[src/main/scala/cpu8086/CPU8086.scala 66:31]
  wire [15:0] _ax_T_1 = {instruction[7:0],8'h0}; // @[src/main/scala/cpu8086/CPU8086.scala 71:35]
  wire [16:0] _result_T = {{1'd0}, ax}; // @[src/main/scala/cpu8086/CPU8086.scala 77:27]
  wire [15:0] result = _result_T[15:0]; // @[src/main/scala/cpu8086/CPU8086.scala 77:27]
  wire [1:0] _GEN_0 = 8'hf4 == opcode ? 2'h2 : state; // @[src/main/scala/cpu8086/CPU8086.scala 68:22 86:17 41:22]
  wire [15:0] _GEN_1 = 8'h1 == opcode ? result : ax; // @[src/main/scala/cpu8086/CPU8086.scala 68:22 78:14 20:19]
  wire [1:0] _GEN_3 = 8'h1 == opcode ? 2'h0 : _GEN_0; // @[src/main/scala/cpu8086/CPU8086.scala 68:22 81:17]
  wire [1:0] _GEN_5 = 8'hb8 == opcode ? 2'h0 : _GEN_3; // @[src/main/scala/cpu8086/CPU8086.scala 68:22 72:17]
  wire  _GEN_12 = 2'h1 == state ? 1'h0 : 2'h2 == state; // @[src/main/scala/cpu8086/CPU8086.scala 50:11 54:17]
  assign io_memAddr = 2'h0 == state ? _io_memAddr_T_2 : _io_memAddr_T_2; // @[src/main/scala/cpu8086/CPU8086.scala 46:14 54:17 57:18]
  assign io_memRead = 2'h0 == state; // @[src/main/scala/cpu8086/CPU8086.scala 54:17]
  assign io_halt = 2'h0 == state ? 1'h0 : _GEN_12; // @[src/main/scala/cpu8086/CPU8086.scala 50:11 54:17]
  assign io_ax = ax; // @[src/main/scala/cpu8086/CPU8086.scala 51:9]
  assign io_ip = ip; // @[src/main/scala/cpu8086/CPU8086.scala 52:9]
  always @(posedge clock) begin
    if (reset) begin // @[src/main/scala/cpu8086/CPU8086.scala 20:19]
      ax <= 16'h0; // @[src/main/scala/cpu8086/CPU8086.scala 20:19]
    end else if (!(2'h0 == state)) begin // @[src/main/scala/cpu8086/CPU8086.scala 54:17]
      if (2'h1 == state) begin // @[src/main/scala/cpu8086/CPU8086.scala 54:17]
        if (8'hb8 == opcode) begin // @[src/main/scala/cpu8086/CPU8086.scala 68:22]
          ax <= _ax_T_1; // @[src/main/scala/cpu8086/CPU8086.scala 71:14]
        end else begin
          ax <= _GEN_1;
        end
      end
    end
    if (reset) begin // @[src/main/scala/cpu8086/CPU8086.scala 36:19]
      ip <= 16'h0; // @[src/main/scala/cpu8086/CPU8086.scala 36:19]
    end else if (2'h0 == state) begin // @[src/main/scala/cpu8086/CPU8086.scala 54:17]
      ip <= _ip_T_1; // @[src/main/scala/cpu8086/CPU8086.scala 60:10]
    end
    if (reset) begin // @[src/main/scala/cpu8086/CPU8086.scala 41:22]
      state <= 2'h0; // @[src/main/scala/cpu8086/CPU8086.scala 41:22]
    end else if (2'h0 == state) begin // @[src/main/scala/cpu8086/CPU8086.scala 54:17]
      state <= 2'h1; // @[src/main/scala/cpu8086/CPU8086.scala 61:13]
    end else if (2'h1 == state) begin // @[src/main/scala/cpu8086/CPU8086.scala 54:17]
      if (opcode != 8'hf4) begin // @[src/main/scala/cpu8086/CPU8086.scala 90:31]
        state <= 2'h0; // @[src/main/scala/cpu8086/CPU8086.scala 91:15]
      end else begin
        state <= _GEN_5;
      end
    end
    if (reset) begin // @[src/main/scala/cpu8086/CPU8086.scala 43:28]
      instruction <= 16'h0; // @[src/main/scala/cpu8086/CPU8086.scala 43:28]
    end else if (2'h0 == state) begin // @[src/main/scala/cpu8086/CPU8086.scala 54:17]
      instruction <= io_memDataIn; // @[src/main/scala/cpu8086/CPU8086.scala 59:19]
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  ax = _RAND_0[15:0];
  _RAND_1 = {1{`RANDOM}};
  ip = _RAND_1[15:0];
  _RAND_2 = {1{`RANDOM}};
  state = _RAND_2[1:0];
  _RAND_3 = {1{`RANDOM}};
  instruction = _RAND_3[15:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module Memory(
  input         clock,
  input  [19:0] io_addr, // @[src/main/scala/cpu8086/CPU8086.scala 103:14]
  output [15:0] io_dataOut, // @[src/main/scala/cpu8086/CPU8086.scala 103:14]
  input         io_read // @[src/main/scala/cpu8086/CPU8086.scala 103:14]
);
`ifdef RANDOMIZE_MEM_INIT
  reg [31:0] _RAND_0;
`endif // RANDOMIZE_MEM_INIT
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
`endif // RANDOMIZE_REG_INIT
  reg [15:0] mem [0:1023]; // @[src/main/scala/cpu8086/CPU8086.scala 111:24]
  wire  mem_io_dataOut_MPORT_en; // @[src/main/scala/cpu8086/CPU8086.scala 111:24]
  wire [9:0] mem_io_dataOut_MPORT_addr; // @[src/main/scala/cpu8086/CPU8086.scala 111:24]
  wire [15:0] mem_io_dataOut_MPORT_data; // @[src/main/scala/cpu8086/CPU8086.scala 111:24]
  wire [15:0] mem_MPORT_data; // @[src/main/scala/cpu8086/CPU8086.scala 111:24]
  wire [9:0] mem_MPORT_addr; // @[src/main/scala/cpu8086/CPU8086.scala 111:24]
  wire  mem_MPORT_mask; // @[src/main/scala/cpu8086/CPU8086.scala 111:24]
  wire  mem_MPORT_en; // @[src/main/scala/cpu8086/CPU8086.scala 111:24]
  reg  mem_io_dataOut_MPORT_en_pipe_0;
  reg [9:0] mem_io_dataOut_MPORT_addr_pipe_0;
  assign mem_io_dataOut_MPORT_en = mem_io_dataOut_MPORT_en_pipe_0;
  assign mem_io_dataOut_MPORT_addr = mem_io_dataOut_MPORT_addr_pipe_0;
  assign mem_io_dataOut_MPORT_data = mem[mem_io_dataOut_MPORT_addr]; // @[src/main/scala/cpu8086/CPU8086.scala 111:24]
  assign mem_MPORT_data = 16'h0;
  assign mem_MPORT_addr = io_addr[9:0];
  assign mem_MPORT_mask = 1'h1;
  assign mem_MPORT_en = 1'h0;
  assign io_dataOut = io_read ? mem_io_dataOut_MPORT_data : 16'h0; // @[src/main/scala/cpu8086/CPU8086.scala 113:14 119:17 120:16]
  always @(posedge clock) begin
    if (mem_MPORT_en & mem_MPORT_mask) begin
      mem[mem_MPORT_addr] <= mem_MPORT_data; // @[src/main/scala/cpu8086/CPU8086.scala 111:24]
    end
    mem_io_dataOut_MPORT_en_pipe_0 <= io_read;
    if (io_read) begin
      mem_io_dataOut_MPORT_addr_pipe_0 <= io_addr[9:0];
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_MEM_INIT
  _RAND_0 = {1{`RANDOM}};
  for (initvar = 0; initvar < 1024; initvar = initvar+1)
    mem[initvar] = _RAND_0[15:0];
`endif // RANDOMIZE_MEM_INIT
`ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  mem_io_dataOut_MPORT_en_pipe_0 = _RAND_1[0:0];
  _RAND_2 = {1{`RANDOM}};
  mem_io_dataOut_MPORT_addr_pipe_0 = _RAND_2[9:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module MyCPU8086System(
  input         clock,
  input         reset,
  output        io_halt, // @[src/main/scala/cpu8086/CPU8086.scala 126:14]
  output [15:0] io_ax, // @[src/main/scala/cpu8086/CPU8086.scala 126:14]
  output [15:0] io_ip // @[src/main/scala/cpu8086/CPU8086.scala 126:14]
);
  wire  cpu_clock; // @[src/main/scala/cpu8086/CPU8086.scala 132:19]
  wire  cpu_reset; // @[src/main/scala/cpu8086/CPU8086.scala 132:19]
  wire [19:0] cpu_io_memAddr; // @[src/main/scala/cpu8086/CPU8086.scala 132:19]
  wire [15:0] cpu_io_memDataIn; // @[src/main/scala/cpu8086/CPU8086.scala 132:19]
  wire  cpu_io_memRead; // @[src/main/scala/cpu8086/CPU8086.scala 132:19]
  wire  cpu_io_halt; // @[src/main/scala/cpu8086/CPU8086.scala 132:19]
  wire [15:0] cpu_io_ax; // @[src/main/scala/cpu8086/CPU8086.scala 132:19]
  wire [15:0] cpu_io_ip; // @[src/main/scala/cpu8086/CPU8086.scala 132:19]
  wire  mem_clock; // @[src/main/scala/cpu8086/CPU8086.scala 133:19]
  wire [19:0] mem_io_addr; // @[src/main/scala/cpu8086/CPU8086.scala 133:19]
  wire [15:0] mem_io_dataOut; // @[src/main/scala/cpu8086/CPU8086.scala 133:19]
  wire  mem_io_read; // @[src/main/scala/cpu8086/CPU8086.scala 133:19]
  MyCPU8086 cpu ( // @[src/main/scala/cpu8086/CPU8086.scala 132:19]
    .clock(cpu_clock),
    .reset(cpu_reset),
    .io_memAddr(cpu_io_memAddr),
    .io_memDataIn(cpu_io_memDataIn),
    .io_memRead(cpu_io_memRead),
    .io_halt(cpu_io_halt),
    .io_ax(cpu_io_ax),
    .io_ip(cpu_io_ip)
  );
  Memory mem ( // @[src/main/scala/cpu8086/CPU8086.scala 133:19]
    .clock(mem_clock),
    .io_addr(mem_io_addr),
    .io_dataOut(mem_io_dataOut),
    .io_read(mem_io_read)
  );
  assign io_halt = cpu_io_halt; // @[src/main/scala/cpu8086/CPU8086.scala 142:11]
  assign io_ax = cpu_io_ax; // @[src/main/scala/cpu8086/CPU8086.scala 143:9]
  assign io_ip = cpu_io_ip; // @[src/main/scala/cpu8086/CPU8086.scala 144:9]
  assign cpu_clock = clock;
  assign cpu_reset = reset;
  assign cpu_io_memDataIn = mem_io_dataOut; // @[src/main/scala/cpu8086/CPU8086.scala 138:20]
  assign mem_clock = clock;
  assign mem_io_addr = cpu_io_memAddr; // @[src/main/scala/cpu8086/CPU8086.scala 136:15]
  assign mem_io_read = cpu_io_memRead; // @[src/main/scala/cpu8086/CPU8086.scala 140:15]
endmodule
