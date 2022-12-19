# MineSQL Repository

采用github的repo分支进行依赖，随项目发布而自动更新。

其他依赖方式见主页介绍。

## 依赖方式

### Maven

```xml
<repositories>
    <repository>
        <id>MineSQL</id>
        <name>GitHub Branch Repository</name>
        <url>https://github.com/CarmJos/MineSQL/blob/repo/</url>
    </repository>
</repositories>
```

### Gradle

```groovy
repositories {
    maven { url 'https://github.com/CarmJos/MineSQL/blob/repo/' }
}
```