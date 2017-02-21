# Certificate Authority Tutorial

This tutorial explains the basic steps to create a Certificate Authority (CA), Intermediate CA and server/client certificates.
This guide is based in the [OpenSSL Certificate Authority Book](https://jamielinux.com/docs/openssl-certificate-authority/index.html).

### Certificate Authority

First is necessary to create the **directory structure**.

```
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

```
cd rethink-ca
openssl genrsa -aes256 -out private/ca.key.pem 4096

Enter pass phrase for ca.key.pem: rethink
Verifying - Enter pass phrase for ca.key.pem: rethink

chmod 400 private/ca.key.pem
```

#### CA root certificate:

```
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

```
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

```
cd rethink-ca
openssl genrsa -aes256 -out intermediate/private/intermediate.key.pem 4096

Enter pass phrase for intermediate.key.pem: rethink
Verifying - Enter pass phrase for intermediate.key.pem: rethink

chmod 400 intermediate/private/intermediate.key.pem
```

#### Intermediate CA Certificate Signing Request (CSR):

Is necessary to generate the CSR, in order to the CA sign the certificate.
The details *should* match the root CA, but the **Common Name** must be different.

```
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

```
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

```
cat intermediate/certs/intermediate.cert.pem \
    certs/ca.cert.pem > intermediate/certs/ca-chain.cert.pem
chmod 444 intermediate/certs/ca-chain.cert.pem
```

### Create server certificate

This is the certificate/key necessary to the Domain Registry.

#### Server key generation:
```
cd rethink-ca
openssl genrsa -aes256 \
    -out intermediate/private/domain.inesc.com.key.pem 2048
chmod 400 intermediate/private/domain.inesc.com.key.pem
```
#### Server Certificate Signing Request (CSR):

Is necessary to generate the CSR, in order to the CA sign the certificate.
The details *should* match the root CA, but the **Common Name** must be different.

```
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

```
cd rethink-ca
openssl ca -config intermediate/openssl.cnf \
    -extensions server_cert -days 375 -notext -md sha256 \
    -in intermediate/csr/domain.inesc.com.csr.pem \
    -out intermediate/certs/domain.inesc.com.cert.pem
chmod 444 intermediate/certs/domain.inesc.com.cert.pem
```
