#!/bin/bash

# 获取提交信息（默认使用当前时间）
read -p "请输入提交信息（默认: 更新于 $(date +'%Y-%m-%d %H:%M:%S')）: " commit_msg
commit_msg=${commit_msg:-"更新于 $(date +'%Y-%m-%d %H:%M:%S')"}

# 执行提交操作
echo "正在添加所有更改..."
git add .

echo "正在提交代码..."
if git commit -m "$commit_msg"; then
    echo "提交成功，准备推送..."
    
    # 获取当前分支名
    current_branch=$(git rev-parse --abbrev-ref HEAD)
    echo "当前分支: $current_branch"
    
    # 推送代码
    if git push origin "$current_branch"; then
        echo "✅ 代码已成功推送到远程仓库！"
    else
        echo "❌ 推送失败，请检查网络或远程仓库配置"
        exit 1
    fi
else
    echo "❌ 提交失败，请检查提交信息或冲突"
    exit 1
fi
