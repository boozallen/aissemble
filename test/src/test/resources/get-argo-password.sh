#!/bin/bash

###
# #%L
# aiSSEMBLE::Test
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
kubectl get secret argocd-initial-admin-secret -o jsonpath='{.data.password}' | base64 -d
echo
