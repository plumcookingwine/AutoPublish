# AutoPublish

## use:

1、讲library工程PublishPlugins，放到项目目录下。 

2、在settings.gradle中添加引用： include ':app', "PublishPlugins"。

3、执行终端命令： gradlew uploadAchivs 。

4、在工程的build.gradle中引入

***
	buildscript {
	
	    ext.kotlin_version = '1.3.21'
	    repositories {
	        google()
	        jcenter()
	        maven {
	            //cooker-plugin 所在的仓库
	            //这里是发布在本地文件夹了
	            url "file://$projectDir/repo"
	        }
	    }
	    dependencies {
	        classpath 'com.android.tools.build:gradle:3.3.2'
	        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
	
	        // custom plugin
	        classpath 'com.plumcookingwine.plugin:PublishPlugins:1.0.4'
	    }
	}
***

5、在app moudule中的build.gradle中使用： 使用自己的配置

***
	apply plugin: 'com.plumcookingwine.plugin'

	upload {
	    pgyerApiKey = "你的apikey（蒲公英）"
	    buildUpdateDescription = "更新描述"
	    // 打包后 APK 路径
	    fileAbsolutePath = "$projectDir\\release\\app-release.apk"
	}
***

6、配置好之后，执行命令  gradlew upload直接上传到蒲公英

最后打印：


![](https://i.imgur.com/LKN5PgW.png)


### 这样每次上传的时候执行 gradlew upload 命令就可以了
