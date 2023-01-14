```text
   __  ____          ________    __ 
  /  |/  (_)__  ___ / __/ __ \  / / 
 / /|_/ / / _ \/ -_)\ \/ /_/ / / /__
/_/  /_/_/_//_/\__/___/\___\_\/____/
#MineSQL (EasySQL-Plugin)
```

# MineSQL (EasySQL-Plugin)

[![version](https://img.shields.io/github/v/release/CarmJos/MineSQL)](https://github.com/CarmJos/MineSQL/releases)
[![License](https://img.shields.io/github/license/CarmJos/MineSQL)](https://opensource.org/licenses/GPL-3.0)
[![workflow](https://github.com/CarmJos/MineSQL/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/CarmJos/MineSQL/actions/workflows/maven.yml)
![CodeSize](https://img.shields.io/github/languages/code-size/CarmJos/MineSQL)
[![CodeFactor](https://www.codefactor.io/repository/github/carmjos/minesql/badge)](https://www.codefactor.io/repository/github/carmjos/minesql)
![](https://visitor-badge.glitch.me/badge?page_id=MineSQL.readme)

轻松(用)SQL的独立运行库插件，支持多种服务端，适用于MineCraft全版本。

## 优势

### 对于服主 (插件使用者)

- 统一配置数据库连接，避免重复配置费时费力分神。
- 支持更高级更全面的配置方式，以根据不同的使
  用场景独立优化[连接池配置](https://github.com/Chris2018998/BeeCP/wiki/Configuration--List)。
- 使相关插件共用连接池，避免每个插件单独新开连接池导致资源的浪费。

### 对于插件开发者

- 基于 [EasySQL](https://github.com/CarmJos/EasySQL) 进行快捷的数据库操作。
- 通过 `MineSQL.getRegistry().get(database-name)` 快捷获取数据源，避免繁琐的链接过程。
- 详细的 DEBUG 方法，找到可恶的错误究竟在哪。
- 不再需要打包各类JDBC-Driver、连接池依赖以及EasySQL本体到插件中。

### 额外提醒

- 使用本依赖时，请保证其他插件的来源安全，避免可能带来的数据安全风险。

## 安装

1. 从 [Releases(发行)](https://github.com/CarmJos/MineSQL/releases/)
   中点击 [最新版](https://github.com/CarmJos/MineSQL/releases/latest) 下载 `MineSQL-x.y.z.jar` 。
2. 将下载的 `MineSQL-x.y.z.jar` 放入服务器 `plugins/` 文件夹下。
3. 启动服务器，预加载配置文件后关闭服务器。
4. 修改 `plugins/MineSQL/config.yml` 以配置您的数据库选项。
5. 启动服务器，若配置无误，则您会看到 MineSQL一切正常的提示消息。

## 配置

### 插件配置文件 [`config.yml`](.doc/example-config.yml)

完整示例配置请见 [示例配置文件](.doc/example-config.yml)。

#### MySQL/MariaDB 数据源详细配置示例

```yaml
sources:
  "mysql-database": # 数据源ID，建议全英文小写并以“-”分隔，例如：hello-minecraft
    # 数据库驱动类型
    # 若您的数据库为 mariadb，则强烈推荐设置为 mariadb
    type: mysql
    host: 127.0.0.1 # 数据库地址
    port: 3306 # 数据库端口
    database: minecraft # 数据库库名
    username: db-user # 数据库用户名
    password: 1234567 #数据库连接密码
```

#### h2 数据源详细配置示例

```yaml
sources:
  # h2数据库运行模式
  # 可选 h2-file(文件模式) 与 h2-mem(内存模式)
  # 文件模式下，须指定 file路径 ，以服务器运行目录为基准，支持绝对路径和相对路径。
  # 内存模式下，所有数据库都将存储在内存中，一旦关服则数据将全部丢失，该模式一般用于测试
  "h2-file-db": # 数据源ID，建议全小写以“-”分隔，例如：hello-minecraft
    type: h2-file
    file-path: "db-files/minecraft"
  "h2-mem-db": # 数据源ID，建议全小写以“-”分隔，例如：hello-minecraft
    type: h2-mem
```

### Properties 配置文件 [`<插件目录>/db-properties/*.properties`](core/src/main/resources/db-properties/.example-mysql.properties)

示例配置请见 [示例MySQL数据源Properties](core/src/main/resources/db-properties/.example-mysql.properties)。

Properties 文件的文件名几位数据源的ID，允许为英文、数字、下划线、短横线；请不要包含中文、其他特殊符号与空格，以`.`开头的文件将被忽略。

该功能一般用于专业开发者使用，若您不了解该功能，请尽量使用config.yml中提供的配置方式，简单便捷，能够满足大多数需求。

更多帮助详见 [BeeCP项目帮助](https://github.com/Chris2018998/BeeCP) 。

## 指令

插件主指令为 `/minesql` ，所有指令只允许后台执行。

```text
# help
- 查看插件指令帮助。

# version
- 查看当前插件、核心库(EasySQL)与连接池依赖版本。

# list
- 列出当前所有的数据源管理器与相关信息。

# info <数据源名称>
- 查看指定数据源的统计信息与当前仍未关闭的查询。
```

## 开发

### 依赖方式

<details>
<summary>展开查看 Maven 依赖方式</summary>

```xml

<project>
    <repositories>

        <repository>
            <!--采用Maven中心库，安全稳定，但版本更新需要等待同步-->
            <id>maven</id>
            <name>Maven Central</name>
            <url>https://repo1.maven.org/maven2</url>
        </repository>

        <repository>
            <!--采用github分支依赖库，稳定更新快-->
            <id>MineSQL</id>
            <name>GitHub Branch Repository</name>
            <url>https://raw.githubusercontent.com/CarmJos/MineSQL/repo/</url>
        </repository>

        <repository>
            <!--采用我的私人依赖库，简单方便，但可能因为变故而无法使用-->
            <id>carm-repo</id>
            <name>Carm's Repo</name>
            <url>https://repo.carm.cc/repository/maven-public/</url>
        </repository>

    </repositories>

    <dependencies>

        <dependency>
            <groupId>cc.carm.plugin</groupId>
            <artifactId>minesql-api</artifactId>
            <version>[LATEST RELEASE]</version>
            <scope>provided</scope>
        </dependency>

    </dependencies>
</project>
```

</details>

<details>
<summary>展开查看 Gradle 依赖方式</summary>

```groovy
repositories {

    //采用Maven中心库，安全稳定，但版本更新需要等待同步
    mavenCentral()

    // 采用github分支依赖库，稳定更新快
    maven { url 'https://github.com/CarmJos/MineSQL/blob/repo/' }

    // 采用我的私人依赖库，简单方便，但可能因为变故而无法使用
    maven { url 'https://repo.carm.cc/repository/maven-public/' }
}

dependencies {
    compileOnly "cc.carm.plugin:minesql-api:[LATEST RELEASE]"
}
```

</details>

### 操作示例

本插件接口入口类为 `MineSQL` ，更多方法详见 [MineSQL-Javadoc](https://carmjos.github.io/MineSQL/) 。

关于 EasySQL的使用方法，请详见 [EasySQL开发文档](https://github.com/CarmJos/EasySQL/tree/master/.documentation) 。


<details>
  <summary>点击查看简单实例</summary>

```java

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {

        SQLManager manager = MineSQL.getRegistery().get(getConfig().getString("database"));

        if (manager == null) {
            System.out.println("请配置一个正确的数据库名。");
            setEnabled(false);
            return;
        }

        // do something...
      
    }

}

```

</details> 

## 开源协议

本项目源码采用 [GNU General Public License v3.0](https://opensource.org/licenses/GPL-3.0) 开源协议。

<details>
  <summary>关于 GPL 协议</summary>

> GNU General Public Licence (GPL) 有可能是开源界最常用的许可模式。GPL 保证了所有开发者的权利，同时为使用者提供了足够的复制，分发，修改的权利：
>
> #### 可自由复制
> 你可以将软件复制到你的电脑，你客户的电脑，或者任何地方。复制份数没有任何限制。
> #### 可自由分发
> 在你的网站提供下载，拷贝到U盘送人，或者将源代码打印出来从窗户扔出去（环保起见，请别这样做）。
> #### 可以用来盈利
> 你可以在分发软件的时候收费，但你必须在收费前向你的客户提供该软件的 GNU GPL 许可协议，以便让他们知道，他们可以从别的渠道免费得到这份软件，以及你收费的理由。
> #### 可自由修改
> 如果你想添加或删除某个功能，没问题，如果你想在别的项目中使用部分代码，也没问题，唯一的要求是，使用了这段代码的项目也必须使用
> GPL 协议。
>
> 需要注意的是，分发的时候，需要明确提供源代码和二进制文件，另外，用于某些程序的某些协议有一些问题和限制，你可以看一下
> @PierreJoye 写的 Practical Guide to GPL Compliance 一文。使用 GPL 协议，你必须在源代码代码中包含相应信息，以及协议本身。
>
> *以上文字来自 [五种开源协议GPL,LGPL,BSD,MIT,Apache](https://www.oschina.net/question/54100_9455) 。*
</details> 
