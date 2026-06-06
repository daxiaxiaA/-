# 数字签名与加密传输软件

本项目是一个基于 JDK 17 和 Swing 的可视化加密传输实验软件，完整实现 `task.png` 中的 A 端签名加密、信道传输、B 端解密验签流程。

## 功能

- 对称加密：支持 AES 和 DES，可在界面中选择。
- Hash 摘要：支持 SHA-256、SHA-1 和 MD5，可在界面中选择。
- RSA 密钥：可生成发送方 A 与接收方 B 两组公私钥，密钥长度支持 2048、1024、512 位，均不小于 200 位。
- 数字签名：A 端计算 `H(M)`，使用 `RK_A` 加密摘要形成签名。
- 加密传输：将 `M || 签名` 使用对称密钥 `K` 加密，再使用 `UK_B` 加密 `K`。
- 解密验签：B 端使用 `RK_B` 解出 `K`，还原 `M || 签名`，再用 `UK_A` 解出签名摘要并和重新计算的 Hash 比较。
- 输入对象：支持字符串和文件两类输入。
- 密文包：支持保存 `.crypto` 密文包，并重新打开密文包完成解密和验签。

## 环境要求

- JDK 17
- Linux、Windows 或 macOS 桌面环境
- 无第三方依赖库

注意：如果系统默认 `java` 是 Java 8，但 `javac` 是 Java 17，需要使用 JDK 17 的 `java` 运行 jar。例如：

```bash
/usr/lib/jvm/java-17-openjdk-amd64/bin/java -jar release/CryptoTaskApp.jar
```

## 构建

```bash
./build.sh
```

构建产物位于：

```text
release/CryptoTaskApp.jar
```

## 运行

```bash
java -jar release/CryptoTaskApp.jar
```

如果默认 Java 版本不是 17，请改用 JDK 17 的 `java`。

## 自检

项目提供命令行自检入口，覆盖 AES/DES 与 SHA-256/SHA-1/MD5 的组合，并测试密文包保存后的重新解码和验签。

```bash
java -jar release/CryptoTaskApp.jar --self-test
```

成功时输出示例：

```text
SELF_TEST_PASSED
cases: 6
```

## 使用说明

1. 启动程序后，选择输入类型、对称算法、Hash 算法和 RSA 密钥长度。
2. 如需固定对称密钥，可输入“对称密钥种子”；留空时由安全随机数生成密钥。
3. 点击“生成 RSA 密钥对”，生成发送方 A 与接收方 B 的公私钥。
4. 在字符串模式下直接输入明文；在文件模式下点击“选择文件”。
5. 点击“执行完整流程”，程序会依次完成 Hash、签名、组合、对称加密、RSA 加密密钥、解密和验签。
6. 过程日志会显示 `H(M)`、签名长度、密文长度、密钥密文长度和验签结果。
7. 点击“保存密文包”可导出 `.crypto` 文件。
8. 点击“打开密文包并验签”可读取已有 `.crypto` 文件并验证。
9. 文件模式下点击“保存解密结果”可导出还原后的文件。

## 目录结构

```text
src/main/java/com/example/cryptotask/
  CryptoTaskApp.java              程序入口
  crypto/                         加密算法、密钥、密文包和工作流
  ui/                             Swing 图形界面
  util/                           字节格式化和输出格式化工具
  selftest/                       命令行自检
docs/                             申报材料草稿和佐证说明
release/CryptoTaskApp.jar         可执行 release 包
build.sh                          构建脚本
```

## 开源许可

本项目使用 MIT License。项目只使用 JDK 标准库，不包含第三方源代码或第三方依赖包。
