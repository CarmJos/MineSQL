```text
 ______                 _____  ____  _        _____  _             _       
|  ____|               / ____|/ __ \| |      |  __ \| |           (_)      
| |__   __ _ ___ _   _| (___ | |  | | |      | |__) | |_   _  __ _ _ _ __  
|  __| / _` / __| | | |\___ \| |  | | |      |  ___/| | | | |/ _` | | '_ \ 
| |___| (_| \__ \ |_| |____) | |__| | |____  | |    | | |_| | (_| | | | | |
|______\__,_|___/\__, |_____/ \___\_\______| |_|    |_|\__,_|\__, |_|_| |_|
                  __/ |                                       __/ |        
                 |___/                                       |___/         
```

<font size="20" color="red">注意：该项目仍在开发中！</font>

# EasySQL-Plugin

轻松(用)SQL的独立运行库插件，支持多种服务端，适用于MineCraft全版本。

## 安装

## 配置

### 插件配置文件 [`config.yml`](easysql-plugin-core/src/main/resources/config.yml)

完整示例配置请见 [源文件](easysql-plugin-core/src/main/resources/config.yml)。

#### MySQL/MariaDB 数据源详细配置示例

```yaml
databases:
  "mysql-database": # 数据源ID，建议全英文小写并以“-”分隔，例如：hello-minecraft
    # 数据库驱动类型
    # 若您的数据库为 mariadb，则强烈推荐设置为 mariadb
    driver-type: mysql
    host: 127.0.0.1 # 数据库地址
    port: 3306 # 数据库端口
    database: minecraft # 数据库库名
    username: db-user # 数据库用户名
    password: 1234567 #数据库连接密码
```

#### h2 数据源详细配置示例

```yaml
databases:
  "h2-database": # 数据源ID，建议全小写以“-”分隔，例如：hello-minecraft
    driver-type: h2
    # h2数据库运行模式
    # 可选 file(文件模式) 与 mem(内存模式)
    # 文件模式下，须指定 file-path ，以服务器运行目录为基准，支持绝对路径和相对路径。
    # 内存模式下，所有数据库都将存储在内存中，一旦关服则数据将全部丢失，该模式一般用于测试
    mode: file # 可选 mem(内存) file(文件) ；内存模式
    file-path: "db-files/minecraft"
```

### Properties 配置文件 [`<插件目录>/db-properties/*.properties`](easysql-plugin-core/src/main/resources/db-properties/.example-mysql.properties)

示例配置请见 [示例MySQL数据源Properties](easysql-plugin-core/src/main/resources/db-properties/.example-mysql.properties)。

该功能一般用于专业开发者使用，若您不了解该功能，请尽量使用config.yml中提供的配置方式，简单便捷，能够满足大多数需求。

更多帮助详见 [BeeCP项目帮助](https://github.com/Chris2018998/BeeCP) 。



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
            <!--采用github依赖库，安全稳定，但需要配置 (推荐)-->
            <id>EasySQL-Plugin</id>
            <name>GitHub Packages</name>
            <url>https://maven.pkg.github.com/CarmJos/EasySQL-Plugin</url>
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
            <artifactId>easysql-plugin-api</artifactId>
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

    // 采用github依赖库，安全稳定，但需要配置 (推荐)
    maven { url 'https://maven.pkg.github.com/CarmJos/EasySQL-Plugin' }

    // 采用我的私人依赖库，简单方便，但可能因为变故而无法使用
    maven { url 'https://repo.carm.cc/repository/maven-public/' }
}

dependencies {
    compileOnly "cc.carm.plugin:easysql-plugin-api:[LATEST RELEASE]"
}
```

</details>

## 指令

插件主指令为 `/easysql` ，所有指令只允许后台执行。

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
> 如果你想添加或删除某个功能，没问题，如果你想在别的项目中使用部分代码，也没问题，唯一的要求是，使用了这段代码的项目也必须使用 GPL 协议。
>
> 需要注意的是，分发的时候，需要明确提供源代码和二进制文件，另外，用于某些程序的某些协议有一些问题和限制，你可以看一下 @PierreJoye 写的 Practical Guide to GPL Compliance 一文。使用 GPL 协议，你必须在源代码代码中包含相应信息，以及协议本身。
>
> *以上文字来自 [五种开源协议GPL,LGPL,BSD,MIT,Apache](https://www.oschina.net/question/54100_9455) 。*
</details>
