package middleware

import (
	"context"
)

type contextKey string

const (
	IdentityIDContextKey contextKey = "identity_id"
	TokenJTIContextKey   contextKey = "token_jti"
)

func SetIdentityID(ctx context.Context, id string) context.Context {
	return context.WithValue(ctx, IdentityIDContextKey, id)
}

func GetIdentityID(ctx context.Context) (string, bool) {
	val, ok := ctx.Value(IdentityIDContextKey).(string)
	return val, ok
}

func SetTokenJTI(ctx context.Context, jti string) context.Context {
	return context.WithValue(ctx, TokenJTIContextKey, jti)
}

func GetTokenJTI(ctx context.Context) (string, bool) {
	val, ok := ctx.Value(TokenJTIContextKey).(string)
	return val, ok
}
