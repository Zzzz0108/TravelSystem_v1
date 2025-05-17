#!/bin/bash

# 检查MySQL是否运行
if ! mysqladmin ping -h localhost -u root --silent; then
    echo "MySQL服务未运行，请先启动MySQL服务"
    exit 1
fi

# 清空并重新创建数据库
echo "清空并重新创建数据库..."
mysql -u root -e "DROP DATABASE IF EXISTS travel_system;"
mysql -u root -e "CREATE DATABASE travel_system;"

# 执行所有SQL文件
for file in *.sql
do
    if [ -f "$file" ] && [ "$file" != "15_comments.sql" ]; then
        echo "正在执行 $file..."
        mysql -u root travel_system < "$file"
        if [ $? -eq 0 ]; then
            echo "$file 执行成功"
        else
            echo "$file 执行失败"
            exit 1
        fi
    fi
done

echo "数据库初始化完成" 