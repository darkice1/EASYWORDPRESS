# EASYWORDPRESS

A high-performance Kotlin library for WordPress REST API integration, designed to simplify and accelerate WordPress development workflows.

## Overview

EASYWORDPRESS is a lightweight, efficient wrapper around the WordPress REST API that provides simplified interfaces for common WordPress operations. Built with Kotlin, it offers enhanced performance through smart caching and intuitive APIs that significantly reduce boilerplate code.

## Key Features

- **Efficient Caching**: Implements Caffeine cache for optimized tag and category operations
- **Smart Tag Management**: Automatic tag creation and retrieval with caching
- **Category Handling**: Streamlined category operations with cache support
- **Type-Safe API**: Fully typed Kotlin API for safer WordPress interactions
- **Performance Optimized**: Minimizes API calls through intelligent caching strategies
- **Easy Integration**: Simple setup process with minimal configuration required

## Getting Started

### Prerequisites

- Kotlin 1.x
- WordPress installation with REST API enabled
- WordPress authentication credentials

### Configuration

Create a `config.properties` file with your WordPress credentials:

```properties
WPBASEURL=your-wordpress-url
WPUSERNAME=your-username
WPUSERPASSWD=your-app-password
```

### Basic Usage

```kotlin
// Initialize the WordPress client
val config = ClientConfig.of(baseUrl, username, appPassword, false, false)
val wp = ClientFactory.fromConfig(config)
val easyWp = EWordpress(wp)

// Work with tags
val tagId = easyWp.getOrCreateTag("your-tag-name")

// Work with categories
val categoryId = easyWp.getOrCreateCategory("your-category-name")
```

## Features in Detail

### Tag Management
- Automatic creation of non-existent tags
- Cache-first retrieval strategy
- Case-insensitive tag matching
- 1-hour cache expiration

### Category Management
- Efficient category creation and retrieval
- Cached category lookups
- Automatic handling of existing categories
- Performance-optimized operations

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support, please open an issue in the GitHub repository.
