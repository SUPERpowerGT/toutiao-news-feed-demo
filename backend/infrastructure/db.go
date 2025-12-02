package infrastructure

import (
	"database/sql"
	"fmt"
	"os"
	"time"

	_ "github.com/jackc/pgx/v5/stdlib"
)

type DBConfig struct {
	Host string
	Port string
	User string
	Pass string
	Name string
}

func LoadDBConfigFromEnv() DBConfig {
	return DBConfig{
		Host: getEnv("DB_HOST", "localhost"),
		Port: getEnv("DB_PORT", "54320"),
		User: getEnv("DB_USER", "toutiao"),
		Pass: getEnv("DB_PASSWORD", "toutiao"),
		Name: getEnv("DB_NAME", "toutiao"),
	}
}

func getEnv(key, def string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return def
}

func NewDB(cfg DBConfig) (*sql.DB, error) {
	dsn := fmt.Sprintf(
		"postgres://%s:%s@%s:%s/%s?sslmode=disable",
		cfg.User, cfg.Pass, cfg.Host, cfg.Port, cfg.Name,
	)

	db, err := sql.Open("pgx", dsn)
	if err != nil {
		return nil, err
	}

	db.SetMaxOpenConns(10)
	db.SetMaxIdleConns(5)
	db.SetConnMaxLifetime(time.Hour)

	if err := db.Ping(); err != nil {
		return nil, err
	}

	return db, nil
}
