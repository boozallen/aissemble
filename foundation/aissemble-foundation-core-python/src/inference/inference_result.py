###
# #%L
# aiSSEMBLE Foundation::aiSSEMBLE Core (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
class InferenceResult:
    """Contains details about the results of an inference request"""

    __threat_detected: bool
    __score: int

    def __init__(self, threat_detected: bool = False, score: int = 0):
        self.threat_detected = threat_detected
        self.score = score

    @property
    def threat_detected(self) -> bool:
        return self.__prediction

    @threat_detected.setter
    def threat_detected(self, new_value: bool):
        self.__prediction = new_value

    @property
    def score(self) -> int:
        return self.__score

    @score.setter
    def score(self, new_value: int):
        self.__score = new_value
