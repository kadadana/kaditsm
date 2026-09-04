package jwks

import (
	"crypto/rsa"
	"encoding/base64"
	"encoding/json"
	"errors"
	"fmt"
	"math/big"
	"net/http"
	"sync"
	"time"

	"github.com/golang-jwt/jwt/v5"
)

type JSONWebKey struct {
	Kty string `json:"kty"`
	Kid string `json:"kid"`
	Use string `json:"use"`
	N   string `json:"n"`
	E   string `json:"e"`
}

type JSONWebKeySet struct {
	Keys []JSONWebKey `json:"keys"`
}

type TokenClaims struct {
	jwt.RegisteredClaims
	SessionID string `json:"session_id,omitempty"`
	IsRevoked bool   `json:"is_revoked,omitempty"`
}

type KeyManager struct {
	jwksURL    string
	httpClient *http.Client
	keys       map[string]*rsa.PublicKey
	mu         sync.RWMutex
}

func NewKeyManager(jwksURL string) (*KeyManager, error) {
	km := &KeyManager{
		jwksURL: jwksURL,
		httpClient: &http.Client{
			Timeout: 5 * time.Second,
		},
		keys: make(map[string]*rsa.PublicKey),
	}

	if err := km.RefreshKeys(); err != nil {
		return nil, fmt.Errorf("initial jwks fetch failed: %w", err)
	}

	return km, nil
}

func (km *KeyManager) RefreshKeys() error {
	resp, err := km.httpClient.Get(km.jwksURL)
	if err != nil {
		return fmt.Errorf("failed to fetch jwks: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return fmt.Errorf("jwks endpoint returned status: %d", resp.StatusCode)
	}

	var jwks JSONWebKeySet
	if err := json.NewDecoder(resp.Body).Decode(&jwks); err != nil {
		return fmt.Errorf("failed to decode jwks response: %w", err)
	}

	newKeys := make(map[string]*rsa.PublicKey)
	for _, key := range jwks.Keys {
		if key.Kty == "RSA" {
			pubKey, err := parseRSAPublicKey(key.N, key.E)
			if err != nil {
				continue
			}
			newKeys[key.Kid] = pubKey
		}
	}

	km.mu.Lock()
	km.keys = newKeys
	km.mu.Unlock()

	return nil
}

func (km *KeyManager) ValidateToken(tokenString string) (*TokenClaims, error) {
	token, err := jwt.ParseWithClaims(tokenString, &TokenClaims{}, func(token *jwt.Token) (interface{}, error) {
		if _, ok := token.Method.(*jwt.SigningMethodRSA); !ok {
			return nil, fmt.Errorf("unexpected signing method: %v", token.Header["alg"])
		}

		kidRaw, ok := token.Header["kid"]
		if !ok {
			return nil, errors.New("missing kid in token header")
		}

		kid, ok := kidRaw.(string)
		if !ok {
			return nil, errors.New("invalid kid format")
		}

		km.mu.RLock()
		pubKey, exists := km.keys[kid]
		km.mu.RUnlock()

		if !exists {
			if err := km.RefreshKeys(); err == nil {
				km.mu.RLock()
				pubKey, exists = km.keys[kid]
				km.mu.RUnlock()
			}
		}

		if !exists {
			return nil, fmt.Errorf("key with kid %s not found", kid)
		}

		return pubKey, nil
	})

	if err != nil {
		return nil, fmt.Errorf("token validation failed: %w", err)
	}

	claims, ok := token.Claims.(*TokenClaims)
	if !ok || !token.Valid {
		return nil, errors.New("invalid token claims")
	}

	return claims, nil
}

func parseRSAPublicKey(nStr, eStr string) (*rsa.PublicKey, error) {
	nBytes, err := base64.RawURLEncoding.DecodeString(nStr)
	if err != nil {
		return nil, err
	}

	eBytes, err := base64.RawURLEncoding.DecodeString(eStr)
	if err != nil {
		return nil, err
	}

	var eInt int
	for _, b := range eBytes {
		eInt = (eInt << 8) | int(b)
	}

	return &rsa.PublicKey{
		N: new(big.Int).SetBytes(nBytes),
		E: eInt,
	}, nil
}
