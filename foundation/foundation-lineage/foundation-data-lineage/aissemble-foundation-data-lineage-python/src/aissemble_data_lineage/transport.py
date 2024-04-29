###
# #%L
# aiSSEMBLE::Foundation::Data Lineage::Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###

from krausening.logging import LogManager
from aissemble_messaging import Message

logger = LogManager.get_instance().get_logger(__name__)


def log_out(event: Message):
    logger.info(msg=event.get_payload())
