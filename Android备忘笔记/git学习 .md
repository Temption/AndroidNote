[TOC]


git init 创建仓库

git add 添加到stage（暂存区）

git commit -m msg （从stage提交到master）

git status 查看状态

git diff HEAD --【file】 比较工作区与库区别git 

checkout -- 【file】从库l恢复到工作区

git reset --hard HEAD^ 上上一个版本就是HEAD^^

git reset --hard 【版本号】 回退到commit之前 之后需要push下

git log --pretty=oneline

master是第一个分支线，HEAD属于指向master分支的一个指针

git remote add origin git@server-name:path/repo-name.git

git clone 地址

git push -u origin master


**Git支持多种协议，包括https，但通过ssh支持的原生git协议速度最快。**

git branch dev创建分支

git branch -d dev  删除分支

git checkout dev 切换到分支

git checkout -b dev  创建并切换分支

git branch 查看分支（当前分支前 有* 标志）

git merge dev （合并dev到当前分支，切回master 再merge）

git stash 保存当前现场后切换其他分支做事情，切回来然后git stash pop/apply回到现场



![git_learn](.\image\git_learn.png)