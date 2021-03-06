# Certificate Authority Tutorial

This tutorial explains the basic steps to create a Certificate Authority (CA), Intermediate CA and server/client certificates.

### This guide is based on the [OpenSSL Certificate Authority Book](https://jamielinux.com/docs/openssl-certificate-authority/index.html).

### Certificate Authority

First is necessary to create the **directory structure**.

```shell
mkdir rethink-ca
cd rethink-ca
mkdir certs crl newcerts private
chmod 700 private
touch index.txt
echo 1000 > serial

```
Download the [openssl-example.cnf](https://github.com/reTHINK-project/dev-registry-domain/blob/master/docs/openssl-example.cnf) file.

This file contains a good basis for the CA configuration.
The main fields that may be adjusted are:

```
[ CA_default ]
# Directory and file locations.
dir               = rethink-ca # the CA main folder name
certs             = $dir/certs
crl_dir           = $dir/crl
new_certs_dir     = $dir/newcerts
database          = $dir/index.txt
serial            = $dir/serial
RANDFILE          = $dir/private/.rand
```

```
# Optionally, specify some defaults.
countryName_default             = PT
stateOrProvinceName_default     = Lisbon
localityName_default            =
0.organizationName_default      = reThink
#organizationalUnitName_default =
#emailAddress_default           =
```

#### CA root key generation:

```shell
cd rethink-ca
openssl genrsa -aes256 -out private/ca.key.pem 4096

Enter pass phrase for ca.key.pem: rethink
Verifying - Enter pass phrase for ca.key.pem: rethink

chmod 400 private/ca.key.pem
```

#### CA root certificate:

```shell
cd rethink-ca
openssl req -config openssl.cnf \
    -key private/ca.key.pem \
    -new -x509 -days 7300 -sha 256 -extensions v3_ca \
    -out certs/ca.cert.pem

Enter pass phrase for ca.key.pem: rethink
You are about to be asked to enter information that will be incorporated
into your certificate request.
-----
Country Name (2 letter code) [XX]: PT
State or Province Name []: Portugal
Locality Name []:
Organization Name []: reThink
Organizational Unit Name []: reThink Certificate Authority
Common Name []: reThink Root CA
Email Address []:

chmod 444 certs/ca.cert.pem
```

### Intermediate Certificate Authority

First is necessary to create the **directory structure**:

```shell
mkdir rethink-ca/intermediate
cd rethink-ca/intermediate

mkdir certs crl csr newcerts private
chmod 700 private
touch index.txt
echo 1000 > serial
echo 1000 > /root/ca/intermediate/crlnumber
```

Download the [openssl-intermediate-example.cnf](https://github.com/reTHINK-project/dev-registry-domain/blob/master/docs/openssl-intermediate-example.cnf) file.

This file contains a good basis for the Intermediate CA configuration.
The main fields that may be adjusted are:

```
[ CA_default ]
# Directory and file locations.
dir               = rethink-ca # the CA main folder name
certs             = $dir/certs
crl_dir           = $dir/crl
new_certs_dir     = $dir/newcerts
database          = $dir/index.txt
serial            = $dir/serial
RANDFILE          = $dir/private/.rand
```

```
# Optionally, specify some defaults.
countryName_default             = PT
stateOrProvinceName_default     = Lisbon
localityName_default            =
0.organizationName_default      = reThink
#organizationalUnitName_default =
#emailAddress_default           =
```

#### Intermediate CA key generation:

```shell
cd rethink-ca
openssl genrsa -aes256 -out intermediate/private/intermediate.key.pem 4096

Enter pass phrase for intermediate.key.pem: rethink
Verifying - Enter pass phrase for intermediate.key.pem: rethink

chmod 400 intermediate/private/intermediate.key.pem
```

#### Intermediate CA Certificate Signing Request (CSR):

Is necessary to generate the CSR, in order to the CA sign the certificate.
The details *should* match the root CA, but the **Common Name** must be different.

```shell
cd rethink-ca
openssl req -config intermediate/openssl.cnf -new -sha256 \
    -key intermediate/private/intermediate.key.pem
    -out intermediate/csr/intermediate.csr.pem

Enter pass phrase for intermediate.key.pem: secretpassword
You are about to be asked to enter information that will be incorporated
into your certificate request.
-----
Country Name (2 letter code) [XX]: PT
State or Province Name []:England
Locality Name []:
Organization Name []: reThink
Organizational Unit Name []: reThink Certificate Authority
Common Name []: reThink Intermediate CA
Email Address []:
```

#### Sign the CSR

```shell
cd rethink-ca
openssl ca -config openssl.cnf -extensions v3_intermediate_ca \
    -days 3650 -notext -md sha256 \
    -in intermediate/csr/intermediate.csr.pem \
    -out intermediate/certs/intermediate.cert.pem

Enter pass phrase for ca.key.pem: rethink
Sign the certificate? [y/n]: y

chmod 444 intermediate/certs/intermediate.cert.pem
```

#### Create Certificate Chain File

Concatenate the intermediate and root certificates together, so is possible to verify the chain of trust.

```shell
cat intermediate/certs/intermediate.cert.pem \
    certs/ca.cert.pem > intermediate/certs/ca-chain.cert.pem
chmod 444 intermediate/certs/ca-chain.cert.pem
```

### Create client/server certificate

This are the certificates necessary to the Domain Registry and Domain Registry Connector.

#### Client/Server key generation:
```shell
cd rethink-ca
openssl genrsa -aes256 \
    -out intermediate/private/domain.inesc.com.key.pem 2048
chmod 400 intermediate/private/domain.inesc.com.key.pem
```
#### Client/Server Certificate Signing Request (CSR):

Is necessary to generate the CSR, in order to the CA sign the certificate.
The details *should* match the root CA, but the **Common Name** must be different.

```shell
cd rethink-ca
openssl req -config intermediate/openssl.cnf \
    -key intermediate/private/domain.inesc.com.key.pem \
    -new -sha256 -out intermediate/csr/domain.inesc.com.csr.pem

Enter pass phrase for domain.inesc.com.key.pem: rethink
You are about to be asked to enter information that will be incorporated
into your certificate request.
-----
Country Name (2 letter code) [XX]:PT
State or Province Name []:Lisbon
Locality Name []:Lisbon View
Organization Name []:reThink
Organizational Unit Name []:INESC reThink
Common Name []:domain.inesc.com
Email Address []:

```

#### Sign the CSR

We need to use the intermediate CA to sign the server CSR.
In the `extensions` argument should be passed `server_cert` if a server certificate, or `usr_cert` if a client certificate.

```shell
cd rethink-ca
openssl ca -config intermediate/openssl.cnf \
    -extensions server_cert -days 375 -notext -md sha256 \
    -in intermediate/csr/domain.inesc.com.csr.pem \
    -out intermediate/certs/domain.inesc.com.cert.pem
chmod 444 intermediate/certs/domain.inesc.com.cert.pem
```

## Usage in Domain Registry

## Instructions
The HAProxy load balancer requires server certificate/key and CA chain for mutual authentication.
In the given example:
 - **Server Key/Certificate**: `rethink-ca/intermediate/certs/domain.lb.pem`
 - **CA Bundle**: `rethink-ca/intermediate/certs/ca-chain.cert.pem`
 
 Is necessary to concatenate the certificates to create the `domain.lb.pem`:
 ```
 cat rethink-ca/intermediate/private/domain.inesc.com.key.pem rethink-ca/intermediate/certs/domain.inesc.com.cert.pem rethink-ca/intermediate/certs/ca-chain.cert.pem > domain.lb.pem
 ```
 
## Usage in Registry Connector

### Node.js instructions
The request library used in node.js supports PEM files, so the generated files can be used directly.
In the given example:
 - **Key File**: `rethink-ca/intermediate/private/connector.key.pem`
 - **Client Certificate**: `rethink-ca/intermediate/certs/connector.cert.pem`
 - **CA Chain**: `rethink-ca/intermediate/certs/ca-chain.cert.pem`
 
 
### Vertx/Java instructions
Vertx/Java request wrapper requires the usage of Java Keystore files.
So first is necessary to create the necessary Keystore and Truststore:

```
# Truststore
keytool -import -file rethink-ca/intermediate/certs/ca-chain.cert.pem -alias ca-chain -keystore truststore.jks -storepass rethink
 
# Keystore
openssl pkcs12 -export -inkey rethink-ca/intermediate/private/connector.key.pem -in rethink-ca/intermediate/certs/connector.cert.pem -name connector-keystore connector.p12
keytool -importkeystore -srckeystore connector.p12 -srcstoretype pkcs12 -destkeystore connector.jks
```
 
