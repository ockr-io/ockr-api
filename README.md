# Ockr API

<p align="left">
<a href="https://ockr-io.github.io/ockr-api/coverage-report/"><img alt="Tests" src="https://github.com/ockr-io/ockr-api/actions/workflows/tests.yaml/badge.svg?branch=main" /></a>
<a href="https://ockr-io.github.io/ockr-api/coverage-report/"><img alt="Coverage" src="https://ockr-io.github.io/ockr-api/badges/jacoco.svg" /></a>
<img alt="Release" src="https://github.com/ockr-io/ockr-api/actions/workflows/release.yaml/badge.svg?branch=main" />
<img alt="Publish" src="https://github.com/ockr-io/ockr-api/actions/workflows/publish.yaml/badge.svg?branch=main" />
<a href="https://conventionalcommits.org"><img alt="conventionalcommits" src="https://img.shields.io/badge/Conventional%20Commits-1.0.0-%23FE5196?logo=conventionalcommits" /></a>
</p>

Ockr aims to set a standard for creating machine-readable and reliable documents, enabling the verification of their authenticity

## Getting Started 🥁

```zsh
git clone https://github.com/ockr-io/ockr-api.git
cd ockr-api
./mvnw clean package
java -jar target/ockr-api-0.2.4.jar
```

## How to use 🧐

- Follow the steps above to start the application 
- Checkout [ockr-ocr-container](https://github.com/ockr-io/ockr-ocr-container) and follow the instructions
- After starting the container, it automatically fetches the needed onnx models from the [ockr-model-zoo](https://github.com/ockr-io/ockr-model-zoo) and registers itself at the ockr-api
- Now you can send a POST request to the ockr-api with the pdf file in the request body:

```mulitpart/form-data
* [POST] localhost:9090/api/v1/pdf/create/qrcode

{
  "file": <file>
}
```

- The response contains the QR code content in text form that need to be placed on the document and will ensure that the document is machine-readable in the future 🤖

## Documentation 🤓

The documentation is available after starting the application at [http://localhost:9090/swagger-ui.html](http://localhost:9090/swagger-ui.html)
