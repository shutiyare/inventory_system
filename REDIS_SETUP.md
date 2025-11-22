# Redis Caching Setup Guide

This document explains the Redis caching implementation in the Inventory System.

## Overview

Redis caching has been implemented to improve application performance by reducing database queries for frequently accessed data. The system uses Spring Cache abstraction with Redis as the cache provider.

## Features

- ✅ **Automatic caching** of frequently accessed data
- ✅ **Cache invalidation** on create/update/delete operations
- ✅ **TTL (Time To Live)** - Cache expires after 1 hour
- ✅ **JSON serialization** for complex objects
- ✅ **Connection pooling** for optimal performance

## Cache Names

The following cache names are used:

- `users` - List of all users
- `user` - Individual user by ID
- `roles` - List of all roles
- `role` - Individual role by ID
- `permissions` - List of all permissions
- `permission` - Individual permission by ID
- `menus` - List of all menus
- `menu` - Individual menu by ID
- `menuTree` - Menu tree structure

## Cached Endpoints

### Users
- `GET /api/users/all` - Cached (list)
- `GET /api/users/{id}` - Cached (by ID)
- `POST /api/users` - Cache evicted
- `PUT /api/users/{id}` - Cache evicted
- `DELETE /api/users/{id}` - Cache evicted

### Roles
- `GET /api/roles/all` - Cached (list)
- `GET /api/roles/{id}` - Cached (by ID)
- `POST /api/roles` - Cache evicted
- `PUT /api/roles/{id}` - Cache evicted
- `DELETE /api/roles/{id}` - Cache evicted

### Permissions
- `GET /api/permissions/all` - Cached (list)
- `GET /api/permissions/{id}` - Cached (by ID)
- `POST /api/permissions` - Cache evicted
- `PUT /api/permissions/{id}` - Cache evicted
- `DELETE /api/permissions/{id}` - Cache evicted

### Menus
- `GET /api/menus/all` - Cached (list)
- `GET /api/menus/tree` - Cached (tree structure)
- `GET /api/menus/{id}` - Cached (by ID)
- `POST /api/menus` - Cache evicted
- `PUT /api/menus/{id}` - Cache evicted
- `DELETE /api/menus/{id}` - Cache evicted

## Installation

### Option 1: Using Docker (Recommended)

```bash
# Start Redis using Docker Compose
docker-compose up redis -d

# Or start all services
docker-compose up -d
```

### Option 2: Local Installation

#### Linux (Ubuntu/Debian)
```bash
sudo apt-get update
sudo apt-get install redis-server
sudo systemctl start redis-server
sudo systemctl enable redis-server
```

#### macOS
```bash
brew install redis
brew services start redis
```

#### Windows
Download and install Redis from: https://github.com/microsoftarchive/redis/releases

Or use WSL:
```bash
sudo apt-get install redis-server
sudo service redis-server start
```

## Configuration

Redis configuration is in `application.properties`:

```properties
# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=
spring.data.redis.timeout=2000ms
spring.data.redis.database=0

# Redis Connection Pool
spring.data.redis.lettuce.pool.enabled=true
spring.data.redis.lettuce.pool.max-active=8
spring.data.redis.lettuce.pool.max-idle=8
spring.data.redis.lettuce.pool.min-idle=0

# Cache Configuration
spring.cache.type=redis
spring.cache.redis.time-to-live=3600000  # 1 hour
spring.cache.redis.cache-null-values=false
spring.cache.redis.key-prefix=inventory:
```

## Verification

### Check Redis Connection
```bash
# Using redis-cli
redis-cli ping
# Should return: PONG
```

### Check Cached Keys
```bash
redis-cli
> KEYS inventory:*
> GET inventory:users
```

### Monitor Cache Operations
```bash
redis-cli MONITOR
```

## Performance Benefits

1. **Reduced Database Load**: Frequently accessed data is served from cache
2. **Faster Response Times**: Cache lookups are much faster than database queries
3. **Better Scalability**: Can handle more concurrent requests
4. **Automatic Invalidation**: Cache is automatically cleared on data changes

## Cache Behavior

### Cache Hit
- Data is retrieved from Redis cache
- No database query is executed
- Response time: ~1-5ms

### Cache Miss
- Data is fetched from database
- Result is stored in cache for future requests
- Response time: ~10-50ms (depends on query complexity)

### Cache Eviction
- Automatically triggered on create/update/delete operations
- Ensures data consistency
- Related caches are cleared

## Troubleshooting

### Redis Connection Failed
```
Error: Unable to connect to Redis
```

**Solution:**
1. Check if Redis is running: `redis-cli ping`
2. Verify host and port in `application.properties`
3. Check firewall settings
4. For Docker: Ensure Redis container is running

### Cache Not Working
**Check:**
1. Redis is running and accessible
2. `@EnableCaching` is present in configuration
3. Cache annotations are on service methods
4. Check application logs for cache-related errors

### Clear All Cache
```bash
redis-cli FLUSHDB
```

### Clear Specific Cache
```bash
redis-cli DEL inventory:users
```

## Monitoring

### Redis CLI Commands
```bash
# Get all keys
redis-cli KEYS "*"

# Get cache statistics
redis-cli INFO stats

# Monitor real-time commands
redis-cli MONITOR

# Get memory usage
redis-cli INFO memory
```

## Production Considerations

1. **Redis Persistence**: Configure AOF (Append Only File) for data durability
2. **Redis Cluster**: Use Redis Cluster for high availability
3. **Memory Management**: Set maxmemory policy (e.g., `allkeys-lru`)
4. **Security**: Enable Redis password authentication
5. **Monitoring**: Use Redis monitoring tools (RedisInsight, etc.)

## Example: Cache Performance

**Without Cache:**
- First request: 45ms (database query)
- Second request: 42ms (database query)
- Third request: 38ms (database query)

**With Cache:**
- First request: 48ms (database query + cache write)
- Second request: 2ms (cache hit)
- Third request: 1ms (cache hit)

**Improvement: ~95% faster for cached requests**

---

**Last Updated:** 2025-11-22


