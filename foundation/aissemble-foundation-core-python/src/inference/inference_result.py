###
# #%L
# aiSSEMBLE Foundation::aiSSEMBLE Core (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
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
