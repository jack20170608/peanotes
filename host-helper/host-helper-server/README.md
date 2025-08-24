# Host helper Server

## Generate the RSA public/private key pair for JWT sign

```shell
$ openssl genrsa -out private.key 2048
$ openssl rsa -in private.key -pubout -out public.key
```
