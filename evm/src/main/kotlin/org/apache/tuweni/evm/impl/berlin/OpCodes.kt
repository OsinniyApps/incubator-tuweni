/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tuweni.evm.impl.berlin

import kotlinx.coroutines.runBlocking
import org.apache.tuweni.bytes.Bytes
import org.apache.tuweni.bytes.Bytes32
import org.apache.tuweni.crypto.Hash
import org.apache.tuweni.eth.Address
import org.apache.tuweni.evm.EVMExecutionStatusCode
import org.apache.tuweni.evm.impl.Opcode
import org.apache.tuweni.evm.impl.Result
import org.apache.tuweni.units.bigints.UInt256
import org.apache.tuweni.units.ethereum.Gas

val add = Opcode { gasManager, _, stack, _, _, _, _ ->
  gasManager.add(3L)
  val item = stack.pop()
  val item2 = stack.pop()
  if (null == item || null == item2) {
    Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  } else {
    stack.push(item.add(item2))
    Result()
  }
}

val addmod = Opcode { gasManager, _, stack, _, _, _, _ ->
  gasManager.add(8L)
  val operand1 = stack.pop()
  val operand2 = stack.pop()
  val mod = stack.pop()
  if (null == operand1 || null == operand2 || null == mod) {
    Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  } else {
    stack.push(if (mod.isZero) UInt256.ZERO else operand1.addMod(operand2, mod))
    Result()
  }
}

val not = Opcode { gasManager, _, stack, _, _, _, _ ->
  gasManager.add(3L)
  val item = stack.pop()
  if (null == item) {
    Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  } else {
    stack.push(item.not())
    Result()
  }
}

val eq = Opcode { gasManager, _, stack, _, _, _, _ ->
  gasManager.add(3L)
  val item = stack.pop()
  val item2 = stack.pop()
  if (null == item || null == item2) {
    Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  } else {
    if (item.equals(item2)) {
      stack.push(UInt256.ONE)
    } else {
      stack.push(UInt256.ZERO)
    }
    Result()
  }
}

val lt = Opcode { gasManager, _, stack, _, _, _, _ ->
  gasManager.add(3L)
  val item = stack.pop()
  val item2 = stack.pop()
  if (null == item || null == item2) {
    Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  } else {
    if (item.lessThan(item2)) {
      stack.push(UInt256.ONE)
    } else {
      stack.push(UInt256.ZERO)
    }
    Result()
  }
}

val slt = Opcode { gasManager, _, stack, _, _, _, _ ->
  gasManager.add(3L)
  val item = stack.pop()
  val item2 = stack.pop()
  if (null == item || null == item2) {
    Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  } else {
    if (item.toBigInteger() < item2.toBigInteger()) {
      stack.push(UInt256.ONE)
    } else {
      stack.push(UInt256.ZERO)
    }
    Result()
  }
}

val gt = Opcode { gasManager, _, stack, _, _, _, _ ->
  gasManager.add(3L)
  val item = stack.pop()
  val item2 = stack.pop()
  if (null == item || null == item2) {
    Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  } else {
    if (item.greaterThan(item2)) {
      stack.push(UInt256.ONE)
    } else {
      stack.push(UInt256.ZERO)
    }
    Result()
  }
}

val sgt = Opcode { gasManager, _, stack, _, _, _, _ ->
  gasManager.add(3L)
  val item = stack.pop()
  val item2 = stack.pop()
  if (null == item || null == item2) {
    Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  } else {
    if (item.toBigInteger() > item2.toBigInteger()) {
      stack.push(UInt256.ONE)
    } else {
      stack.push(UInt256.ZERO)
    }
    Result()
  }
}

val isZero = Opcode { gasManager, _, stack, _, _, _, _ ->
  gasManager.add(3L)
  val item = stack.pop()
  if (null == item) {
    Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  } else {
    stack.push(if (item.isZero) UInt256.ONE else UInt256.ZERO)
    Result()
  }
}

val and = Opcode { gasManager, _, stack, _, _, _, _ ->
  gasManager.add(3L)
  val item = stack.pop()
  val item2 = stack.pop()
  if (null == item || null == item2) {
    Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  } else {
    stack.push(item.and(item2))
    Result()
  }
}

val pop = Opcode { gasManager, _, stack, _, _, _, _ ->
  gasManager.add(2L)
  stack.pop() ?: return@Opcode Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  Result()
}

val or = Opcode { gasManager, _, stack, _, _, _, _ ->
  gasManager.add(3L)
  val item = stack.pop()
  val item2 = stack.pop()
  if (null == item || null == item2) {
    Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  } else {
    stack.push(item.or(item2))
    Result()
  }
}

val xor = Opcode { gasManager, _, stack, _, _, _, _ ->
  gasManager.add(3L)
  val item = stack.pop()
  val item2 = stack.pop()
  if (null == item || null == item2) {
    Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  } else {
    stack.push(item.xor(item2))
    Result()
  }
}

val byte = Opcode { gasManager, _, stack, _, _, _, _ ->
  gasManager.add(3L)
  val offset = stack.pop()
  val stackElement = stack.pop()
  if (null == offset || null == stackElement) {
    Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  } else {
    if (!offset.fitsInt() || offset.intValue() >= 32) {
      stack.push(UInt256.ZERO)
    } else {
      stack.push(Bytes32.leftPad(Bytes.of(stackElement.get(offset.intValue()))))
    }
    Result()
  }
}

val mul = Opcode { gasManager, _, stack, _, _, _, _ ->
  gasManager.add(5L)
  val item = stack.pop()
  val item2 = stack.pop()
  if (null == item || null == item2) {
    Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  } else {
    stack.push(item.multiply(item2))
    Result()
  }
}

val mod = Opcode { gasManager, _, stack, _, _, _, _ ->
  gasManager.add(5L)
  val item = stack.pop()
  val item2 = stack.pop()
  if (null == item || null == item2) {
    Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  } else {
    stack.push(item.mod0(item2))
    Result()
  }
}

val smod = Opcode { gasManager, _, stack, _, _, _, _ ->
  gasManager.add(5L)
  val item = stack.pop()
  val item2 = stack.pop()
  if (null == item || null == item2) {
    Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  } else {
    stack.push(item.smod0(item2))
    Result()
  }
}

val mulmod = Opcode { gasManager, _, stack, _, _, _, _ ->
  gasManager.add(8L)
  val item = stack.pop()
  val item2 = stack.pop()
  val item3 = stack.pop()
  if (null == item || null == item2 || null == item3) {
    Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  } else {
    if (item3.isZero) {
      stack.push(Bytes32.ZERO)
    } else {
      stack.push(item.multiplyMod(item2, item3))
    }
    Result()
  }
}

val sub = Opcode { gasManager, _, stack, _, _, _, _ ->
  gasManager.add(3L)
  val item = stack.pop()
  val item2 = stack.pop()
  if (null == item || null == item2) {
    Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  } else {
    stack.push(item.subtract(item2))
    Result()
  }
}

val exp = Opcode { gasManager, _, stack, _, _, _, _ ->
  val number = stack.pop()
  val power = stack.pop()
  if (null == number || null == power) {
    Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  } else {
    val numBytes: Int = (power.bitLength() + 7) / 8

    val cost = (Gas.valueOf(50).multiply(Gas.valueOf(numBytes.toLong())))
      .add(Gas.valueOf(10))
    gasManager.add(cost)

    val result: UInt256 = number.pow(power)

    stack.push(result)
    Result()
  }
}

val div = Opcode { gasManager, _, stack, _, _, _, _ ->
  gasManager.add(5L)
  val item = stack.pop()
  val item2 = stack.pop()
  if (null == item || null == item2) {
    Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  } else {
    stack.push(if (item2.isZero) UInt256.ZERO else item.divide(item2))
    Result()
  }
}

fun push(length: Int): Opcode {
  return Opcode { gasManager, _, stack, _, code, currentIndex, _ ->
    gasManager.add(3)
    val minLength = Math.min(length, code.size() - currentIndex)
    stack.push(Bytes32.leftPad(code.slice(currentIndex, minLength)))
    Result(newCodePosition = currentIndex + minLength)
  }
}

fun dup(index: Int): Opcode {
  return Opcode { gasManager, _, stack, _, _, _, _ ->
    gasManager.add(3)
    val value = stack.get(index - 1) ?: return@Opcode Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
    stack.push(value)
    Result()
  }
}

fun swap(index: Int): Opcode {
  return Opcode { gasManager, _, stack, _, _, _, _ ->
    gasManager.add(3L)
    val eltN = stack.get(index) ?: return@Opcode Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
    val elt0 = stack.pop() ?: return@Opcode Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
    stack.push(eltN)
    stack.set(index, elt0)
    Result()
  }
}

val sstore = Opcode { gasManager, hostContext, stack, msg, _, _, _ ->
  runBlocking {
    val key = stack.pop()
    val value = stack.pop()
    if (null == key || null == value) {
      return@runBlocking Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
    }

    val address = msg.destination
    val slotIsWarm = hostContext.warmUpStorage(address, key)

    val currentValue = hostContext.getStorage(address, key)
    val cost = if (value.equals(currentValue)) {
      Gas.valueOf(100)
    } else {
      val originalValue = hostContext.getRepositoryStorage(address, key)
      if (originalValue.equals(currentValue)) {
        if (originalValue.isZero) {
          Gas.valueOf(20000 - 2100)
        } else Gas.valueOf(5000 - 2100)
      } else {
        Gas.valueOf(100)
      }
    }.add(if (slotIsWarm) Gas.ZERO else Gas.valueOf(2100))
    gasManager.add(cost)

    val remainingGas = gasManager.gasLeft()
    if (remainingGas <= 2300) {
      return@runBlocking Result(EVMExecutionStatusCode.OUT_OF_GAS)
    }

    // frame.incrementGasRefund(gasCalculator().calculateStorageRefundAmount(account, key, value))

    hostContext.setStorage(address, key, value)

    Result()
  }
}

val sload = Opcode { gasManager, hostContext, stack, msg, _, _, _ ->
  val key = stack.pop() ?: return@Opcode Result(EVMExecutionStatusCode.STACK_UNDERFLOW)

  val address = msg.destination
  val slotIsWarm = hostContext.warmUpStorage(address, key)
  gasManager.add(if (slotIsWarm) 100 else 2600)

  runBlocking {
    stack.push(hostContext.getStorage(address, key))
  }
  Result()
}

val stop = Opcode { _, _, _, _, _, _, _ ->
  Result(EVMExecutionStatusCode.SUCCESS)
}

val invalid = Opcode { _, _, _, _, _, _, _ ->
  Result(EVMExecutionStatusCode.INVALID_INSTRUCTION)
}

val retuRn = Opcode { gasManager, _, stack, _, _, _, memory ->
  val location = stack.pop()
  val length = stack.pop()
  if (null == location || null == length) {
    return@Opcode Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  }
  val pre = memoryCost(memory.size())
  val post: Gas = memoryCost(memory.newSize(location, length))
  gasManager.add(post.subtract(pre))
  val output = memory.read(location, length)
  Result(EVMExecutionStatusCode.SUCCESS, output = output)
}

val address = Opcode { gasManager, _, stack, msg, _, _, _ ->
  gasManager.add(2)
  stack.push(Bytes32.leftPad(msg.destination))
  Result()
}

val origin = Opcode { gasManager, _, stack, msg, _, _, _ ->
  gasManager.add(2)
  stack.push(Bytes32.leftPad(msg.sender))
  Result()
}

val caller = Opcode { gasManager, _, stack, msg, _, _, _ ->
  gasManager.add(2)
  stack.push(Bytes32.leftPad(msg.sender))
  Result()
}

val callvalue = Opcode { gasManager, _, stack, msg, _, _, _ ->
  gasManager.add(2)
  stack.push(Bytes32.leftPad(msg.value))
  Result()
}

val balance = Opcode { gasManager, hostContext, stack, _, _, _, _ ->

  val address = stack.pop()?.slice(12, 20)?.let { Address.fromBytes(it) }
    ?: return@Opcode Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  val accountIsWarm = hostContext.warmUpAccount(address)
  if (accountIsWarm) {
    gasManager.add(100)
  } else {
    gasManager.add(2600)
  }

  runBlocking {
    stack.push(hostContext.getBalance(address))
  }
  Result()
}

val pc = Opcode { gasManager, _, stack, _, _, currentIndex, _ ->
  gasManager.add(2)
  stack.push(UInt256.valueOf(currentIndex.toLong() - 1))
  Result()
}

val gasPrice = Opcode { gasManager, hostContext, stack, _, _, _, _ ->
  gasManager.add(2)
  stack.push(Bytes32.leftPad(hostContext.getGasPrice()))
  Result()
}

val gas = Opcode { gasManager, _, stack, _, _, _, _ ->
  gasManager.add(2)
  stack.push(UInt256.valueOf(gasManager.gasLeft().toLong()))
  Result()
}

val coinbase = Opcode { gasManager, hostContext, stack, _, _, _, _ ->
  gasManager.add(2)
  stack.push(Bytes32.leftPad(hostContext.getCoinbase()))
  Result()
}

val gasLimit = Opcode { gasManager, hostContext, stack, _, _, _, _ ->
  gasManager.add(2)
  stack.push(UInt256.valueOf(hostContext.getGasLimit()))
  Result()
}

val difficulty = Opcode { gasManager, hostContext, stack, _, _, _, _ ->
  gasManager.add(2)
  stack.push(hostContext.getDifficulty())
  Result()
}

val number = Opcode { gasManager, hostContext, stack, _, _, _, _ ->
  gasManager.add(2)
  stack.push(UInt256.valueOf(hostContext.getBlockNumber()))
  Result()
}

val blockhash = Opcode { gasManager, hostContext, stack, _, _, _, _ ->
  gasManager.add(20)
  val number = stack.pop() ?: return@Opcode Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  if (!number.fitsLong() || number.toLong() < hostContext.getBlockNumber() - 256) {
    stack.push(UInt256.ZERO)
  } else {
    stack.push(UInt256.fromBytes(hostContext.getBlockHash(number.toLong())))
  }
  Result()
}

val codesize = Opcode { gasManager, _, stack, _, code, _, _ ->
  gasManager.add(2)
  stack.push(UInt256.valueOf(code.size().toLong()))
  Result()
}

val timestamp = Opcode { gasManager, hostContext, stack, _, _, _, _ ->
  gasManager.add(2)
  stack.push(hostContext.timestamp())
  Result()
}

fun memoryCost(length: UInt256): Gas {
  if (!length.fitsInt()) {
    return Gas.TOO_HIGH
  }
  val len: Gas = Gas.valueOf(length)
  val base: Gas = len.multiply(len).divide(Gas.valueOf(512))
  return Gas.valueOf(3).multiply(len).add(base)
}

val codecopy = Opcode { gasManager, _, stack, _, code, _, memory ->
  val memOffset = stack.pop()
  val sourceOffset = stack.pop()
  val length = stack.pop()
  if (null == memOffset || null == sourceOffset || null == length) {
    return@Opcode Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  }
  val numWords: UInt256 = length.divideCeil(Bytes32.SIZE.toLong())
  val copyCost = Gas.valueOf(3).multiply(Gas.valueOf(numWords)).add(Gas.valueOf(3))
  val pre = memoryCost(memory.size())
  val post: Gas = memoryCost(memory.newSize(memOffset, length))
  val memoryCost = post.subtract(pre)

  gasManager.add(copyCost.add(memoryCost))

  memory.write(memOffset, sourceOffset, length, code)

  Result()
}

val mstore = Opcode { gasManager, _, stack, _, _, _, memory ->
  val location = stack.pop()
  val value = stack.pop()
  if (null == location || null == value) {
    return@Opcode Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  }
  val pre = memoryCost(memory.size())
  val post = memoryCost(memory.newSize(location, UInt256.valueOf(32)))
  val memoryCost = post.subtract(pre)
  gasManager.add(Gas.valueOf(3L).add(memoryCost))

  memory.write(location, UInt256.ZERO, UInt256.valueOf(32), value)
  Result()
}

val mstore8 = Opcode { gasManager, _, stack, _, _, _, memory ->
  val location = stack.pop()
  val value = stack.pop()
  if (null == location || null == value) {
    return@Opcode Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  }
  val pre = memoryCost(memory.size())
  val post: Gas = memoryCost(memory.newSize(location, UInt256.valueOf(1)))
  val memoryCost = post.subtract(pre)
  gasManager.add(Gas.valueOf(3L).add(memoryCost))

  memory.write(location, UInt256.ZERO, UInt256.valueOf(1), value.slice(31, 1))
  Result()
}

val mload = Opcode { gasManager, _, stack, _, _, _, memory ->
  val location = stack.pop() ?: return@Opcode Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  val pre = memoryCost(memory.size())
  val post: Gas = memoryCost(memory.newSize(location, UInt256.valueOf(32)))
  val memoryCost = post.subtract(pre)
  gasManager.add(Gas.valueOf(3L).add(memoryCost))

  stack.push(Bytes32.leftPad(memory.read(location, UInt256.valueOf(32)) ?: Bytes.EMPTY))
  Result()
}

val extcodesize = Opcode { gasManager, hostContext, stack, msg, _, _, _ ->
  gasManager.add(700)
  runBlocking {
    stack.push(UInt256.valueOf(hostContext.getCode(msg.destination).size().toLong()))
    Result()
  }
}

val msize = Opcode { gasManager, _, stack, _, _, _, memory ->
  gasManager.add(2)
  stack.push(memory.allocatedBytes())
  Result()
}

val calldatasize = Opcode { gasManager, _, stack, msg, _, _, _ ->
  gasManager.add(2)
  stack.push(UInt256.valueOf(msg.inputData.size().toLong()))
  Result()
}

val calldatacopy = Opcode { gasManager, _, stack, msg, _, _, memory ->
  val memOffset = stack.pop()
  val sourceOffset = stack.pop()
  val length = stack.pop()
  if (null == memOffset || null == sourceOffset || null == length) {
    return@Opcode Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  }
  val numWords: UInt256 = length.divideCeil(Bytes32.SIZE.toLong())
  val copyCost = Gas.valueOf(3).multiply(Gas.valueOf(numWords)).add(Gas.valueOf(3))
  val pre = memoryCost(memory.size())
  val post = memoryCost(memory.newSize(memOffset, length))
  val memoryCost = post.subtract(pre)

  gasManager.add(copyCost.add(memoryCost))

  memory.write(memOffset, sourceOffset, length, msg.inputData)

  Result()
}

val calldataload = Opcode { gasManager, _, stack, msg, _, _, _ ->
  gasManager.add(3)
  val start = stack.pop() ?: return@Opcode Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  var set = false
  if (start.fitsInt()) {
    if (msg.inputData.size() > start.intValue()) {
      stack.push(
        Bytes32.rightPad(
          msg.inputData.slice(
            start.intValue(),
            Math.min(32, msg.inputData.size() - start.intValue())
          )
        )
      )
      set = true
    }
  }
  if (!set) {
    stack.push(Bytes32.ZERO)
  }
  Result()
}

val sha3 = Opcode { gasManager, _, stack, _, _, _, memory ->
  val from = stack.pop()
  val length = stack.pop()
  if (null == from || null == length) {
    return@Opcode Result(EVMExecutionStatusCode.OUT_OF_GAS)
  }
  val numWords: UInt256 = length.divideCeil(Bytes32.SIZE.toLong())
  val copyCost = Gas.valueOf(6).multiply(Gas.valueOf(numWords)).add(Gas.valueOf(30))
  val pre = memoryCost(memory.size())
  val post: Gas = memoryCost(memory.newSize(from, length))
  val memoryCost = post.subtract(pre)
  gasManager.add(copyCost.add(memoryCost))
  val bytes = memory.read(from, length)
  stack.push(if (bytes == null) Bytes32.ZERO else Hash.keccak256(bytes))
  Result()
}

fun computeValidJumpDestinations(code: Bytes): Set<Int> {
  var index = 0
  val destinations = HashSet<Int>()
  while (index < code.size()) {
    val currentOpcode = code.get(index)
    if (currentOpcode == 0x5b.toByte()) {
      destinations.add(index)
    }
    if (currentOpcode.toInt() >= 0x60 && currentOpcode < 0x80) {
      index += currentOpcode - 0x60 + 1
    }
    index++
  }

  return destinations
}

val jump = Opcode { gasManager, _, stack, _, code, _, _ ->
  gasManager.add(8)
  val jumpDest = stack.pop() ?: return@Opcode Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  if (!jumpDest.fitsInt() || jumpDest.intValue() >= code.size()) {
    return@Opcode Result(EVMExecutionStatusCode.BAD_JUMP_DESTINATION)
  }
  val validDestinations = computeValidJumpDestinations(code)
  if (!validDestinations.contains(jumpDest.intValue())) {
    return@Opcode Result(EVMExecutionStatusCode.BAD_JUMP_DESTINATION)
  }
  Result(newCodePosition = jumpDest.intValue())
}

val jumpi = Opcode { gasManager, _, stack, _, code, _, _ ->
  gasManager.add(10)
  val jumpDest = stack.pop() ?: return@Opcode Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  val condition = stack.pop() ?: return@Opcode Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  if (condition.isZero) {
    return@Opcode Result()
  }
  if (!jumpDest.fitsInt() || jumpDest.intValue() >= code.size()) {
    return@Opcode Result(EVMExecutionStatusCode.BAD_JUMP_DESTINATION)
  }
  val validDestinations = computeValidJumpDestinations(code)
  if (!validDestinations.contains(jumpDest.intValue())) {
    return@Opcode Result(EVMExecutionStatusCode.BAD_JUMP_DESTINATION)
  }
  Result(newCodePosition = jumpDest.intValue())
}

val jumpdest = Opcode { gasManager, _, _, _, _, _, _ ->
  gasManager.add(1)
  Result()
}

fun log(topics: Int): Opcode {
  return Opcode { gasManager, hostContext, stack, msg, _, _, memory ->
    val location = stack.pop()
    val length = stack.pop()
    if (null == location || null == length) {
      return@Opcode Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
    }

    val cost = Gas.valueOf(375).add((Gas.valueOf(8).multiply(Gas.valueOf(length)))).add(
      Gas.valueOf(375)
        .multiply(
          Gas.valueOf(
            topics.toLong()
          )
        )
    )
    val pre = memoryCost(memory.size())
    val post: Gas = memoryCost(memory.newSize(location, length))
    gasManager.add(cost.add(post.subtract(pre)))
    val address = msg.destination

    val data = memory.read(location, length)

    val topicList = mutableListOf<Bytes32>()
    for (i in 0 until topics) {
      topicList.add(stack.pop() ?: return@Opcode Result(EVMExecutionStatusCode.STACK_UNDERFLOW))
    }

    hostContext.emitLog(address, data ?: Bytes.EMPTY, topicList.toList())

    if (data == null) {
      Result(validationStatus = EVMExecutionStatusCode.INVALID_MEMORY_ACCESS)
    } else {
      Result()
    }
  }
}

val sdiv = Opcode { gasManager, _, stack, _, _, _, _ ->
  gasManager.add(5L)
  val item = stack.pop()
  val item2 = stack.pop()
  if (null == item || null == item2) {
    Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  } else {
    stack.push(item.sdiv0(item2))
    Result()
  }
}

val signextend = Opcode { gasManager, _, stack, _, _, _, _ ->
  gasManager.add(5L)
  val item = stack.pop()
  val item2 = stack.pop()
  if (null == item || null == item2) {
    Result(EVMExecutionStatusCode.STACK_UNDERFLOW)
  } else {
    if (!item.fitsInt() || item.intValue() > 31) {
      stack.push(item2)
    } else {
      val byteIndex: Int = 32 - 1 - item.getInt(32 - 4)
      stack.push(
        UInt256.fromBytes(
          Bytes32.leftPad(
            item2.slice(byteIndex),
            if (item2.get(byteIndex) < 0) 0xFF.toByte() else 0x00
          )
        )
      )
    }

    Result()
  }
}

val selfdestruct = Opcode { gasManager, hostContext, stack, msg, _, _, _ ->
  val recipientAddress = stack.pop()?.slice(12, 20)?.let { Address.fromBytes(it) } ?: return@Opcode Result(
    EVMExecutionStatusCode.STACK_UNDERFLOW
  )

  runBlocking {

    val inheritance = hostContext.getBalance(recipientAddress)

    val accountIsWarm = hostContext.warmUpAccount(recipientAddress)

    val cost = if (hostContext.accountExists(recipientAddress) && !inheritance.isZero) {
      Gas.valueOf(30000)
    } else {
      Gas.valueOf(5000)
    }.add(if (accountIsWarm) Gas.ZERO else Gas.valueOf(2600))
    gasManager.add(cost)
    val address: Address = msg.destination

    hostContext.selfdestruct(address, recipientAddress)

    // frame.addRefund(recipient.getAddress(), account.getBalance())

    Result(EVMExecutionStatusCode.SUCCESS)
  }
}
