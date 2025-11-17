## 11/16-11/17

1、完成了01-10的开发文档的初步定稿，初步确定开发内容以及计划，后续再开发过程当中更新相关内容

2、配置git仓库初始化文件目录开始项目管理

```
news-feed-demo/
│
├── docs/           # 软件工程文档（需求、用例、架构、设计…）
│
├── backend/        # Go 后端（我们的 API 服务）
│
├── android/        # Android Studio 工程（Compose App）
│
└── README.md       # 项目总说明

```

仓库初始化

```
git init
git add .
git commit -m "Init project structure"

```

创建远程仓库

关联远程仓库

```
git branch -M main
git remote add origin https://github.com/SUPERpowerGT/toutiao-news-feed-demo.git
git push -u origin main
```

https://github.com/SUPERpowerGT/toutiao-news-feed-demo

3、启动jira来管理项目sprint以及每日功能开发需求更新

https://toutiao-develop.atlassian.net/jira/software/projects/SCRUM/boards/1

4、环境配置

后端环境配置

vscode

- go
- docker

go

```
go version go1.24.0 windows/amd64
```

postgresql

- docker拉去镜像配置用pgadmin来可视化

postman

docker desktop



客户端开发环境（windows）安卓

android studio

 https://developer.android.com/studio

配置时候注意改在d盘同一目录下包括后续sdk安装等



5、客户端开发

新建项目这里注意要修改名称和路径以及vpn不建议挂

更新gitignore再根目录（全局管理项目删掉子目录自动生成的gitignore）

```
git rm -r --cached .

```

