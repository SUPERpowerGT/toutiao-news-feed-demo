FROM golang:1.24-alpine

WORKDIR /app

# 1. 复制依赖定义并下载
COPY go.mod go.sum ./
RUN go mod download

# 2. 复制源码
COPY . .

# 3. 编译二进制
RUN go build -o server .

# 4. 启动命令
CMD ["./server"]
