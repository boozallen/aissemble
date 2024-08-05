###
# #%L
# aiSSEMBLE Foundation::aiSSEMBLE Core (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
import jwt
import jks
import errno
import os
import base64, textwrap
import time
from .auth_config import AuthConfig

config = AuthConfig()


class AissembleSecurityException(Exception):
    pass


class JsonWebTokenUtil:
    """
    Utility for parsing jwt for authentication
    """

    def __init__(self):
        pass

    def parse_token(self, token: str):
        """
        Parses the token using a public key which is specified in the auth.properties.
        Will raise exception if the token is not correctly signed, the token has expired,
        or the token is not a parsable jwt.
        """
        pub_key_path = None
        pub_key = None
        try:
            pub_key_path = config.public_key_path()
        except KeyError:
            raise FileNotFoundError(
                errno.ENOENT,
                os.strerror(errno.ENOENT),
                "public_key.pem (public_key_path not configured in auth.properties)",
            )

        if not os.path.exists(pub_key_path):
            raise FileNotFoundError(
                errno.ENOENT,
                os.strerror(errno.ENOENT),
                "public_key.pem (public_key.pem not found.  Are you sure the path is correct in auth.properties?)",
            )

        with open(pub_key_path, "rb") as pub_key_file:
            pub_key = pub_key_file.read()

        decoded = jwt.decode(token, pub_key, algorithms=["RS256"], audience="audience")

        return decoded

    def create_token(self):
        """
        This is a convenience method which will generate a valid token with hardcoded subject.
        """
        key = self.get_sign_key()
        claim = {
            "jti": "7d6f1fe5-9c6b-45da-89f1-2039ab05fa41",
            "sub": "aissemble",
            "aud": "audience",
            "nbf": time.time(),
            "iat": time.time(),
            "exp": time.time() + 604800,  # 1 week
            "iss": "aissemble.authority",
        }

        token = jwt.encode(claim, key, algorithm="RS256")
        print(token)
        return token

    def validate_token(self, token: str) -> None:
        """
        Validates the given token and will raise an AissembleSecurityException when the token fails validation.
        """
        auth_exception_details = None
        try:
            self.parse_token(token)
        except jwt.ExpiredSignatureError:
            auth_exception_details = (
                "Invalid authentication credentials (expired token)"
            )
        except jwt.InvalidIssuerError:
            auth_exception_details = (
                "Invalid authentication credentials (unrecognized issuer)"
            )
        except jwt.InvalidAlgorithmError:
            auth_exception_details = (
                "Invalid authentication credentials (invalid algorithm - must be RS256)"
            )
        except jwt.DecodeError:
            auth_exception_details = (
                "Invalid authentication credentials (token not parsable)"
            )

        if auth_exception_details:
            print(auth_exception_details)
            raise AissembleSecurityException(auth_exception_details)

    def get_sign_key(self) -> str:
        """
        This method retrieves the signing key from a java keystore which is specified in auth.properties
        """
        jks_path = None
        jks_password = config.jks_password()
        key_alias = config.jks_key_alias()
        try:
            jks_path = config.jks_path()
        except KeyError:
            raise FileNotFoundError(
                errno.ENOENT,
                os.strerror(errno.ENOENT),
                "*.jks (keystore not configured in auth.properties)",
            )

        if not os.path.exists(jks_path):
            raise FileNotFoundError(
                errno.ENOENT,
                os.strerror(errno.ENOENT),
                jks_path
                + "(jks not found.  Are you sure the path is correct in auth.properties?)",
            )

        ks = jks.KeyStore.load(jks_path, jks_password)

        pk_entry = ks.private_keys[key_alias]
        key_string = (
            "-----BEGIN RSA PRIVATE KEY-----\n"
            + "\n".join(
                textwrap.wrap(base64.b64encode(pk_entry.pkey).decode("ascii"), 64)
            )
            + "\n-----END RSA PRIVATE KEY-----"
        )
        return key_string.encode("utf_8")
