# Quarkus Proxy

A lightweight, efficient, and scalable proxy built with Quarkus and Vert.x. This proxy is designed to fetch content from the Quarkus website, modify it on-the-fly, and serve it to clients, ensuring that navigation remains within the proxy.

## Features

- **Content Modification**: Adds a "™" symbol to six-letter words that are not inside HTML tags.
- **Internal Link Redirection**: Modifies internal links to ensure that navigation stays within the proxy.
- **Image Proxying**: Efficiently proxies image content from the original site.
- **Redirect Handling**: Seamlessly handles HTTP redirects to ensure a smooth user experience.
- **Performance**: Built with Quarkus and Vert.x for high performance and low resource consumption.

## Getting Started

### Prerequisites

- Java 17
- Gradle

### Building the Project

1. Clone the repository:
   `git clone https://github.com/MAXSSYPE/quarkus-proxy.git`

2. Build the project using Gradle:
   `./gradlew build`


3. Run the application:
   `./gradlew quarkusDev`


4. Access the proxy at: `http://localhost:8080/proxy/`

## Usage

Simply navigate to `http://localhost:8080/proxy/` followed by the path you want to proxy from the Quarkus website. For example, to proxy the Quarkus guides, go to `http://localhost:8080/proxy/guides/`.
