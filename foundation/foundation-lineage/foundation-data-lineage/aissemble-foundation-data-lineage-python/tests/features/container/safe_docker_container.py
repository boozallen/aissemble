from testcontainers.core.container import DockerContainer


class SafeDockerContainer(DockerContainer):
    """
    There is currently a bug in the Test Containers library that, due to a race condition, can cause
    an attribute error when container objects are being torn down by Python.  This just modified the
    teardown logic to ensure the attr exists.

    See <https://github.com/testcontainers/testcontainers-python/issues/353>
    """

    def __init__(self, image: str, **kwargs) -> None:
        super().__init__(image, **kwargs)

    def __del__(self) -> None:
        """
        Try to remove the container in all circumstances
        """
        if hasattr(self, "_container") and self._container is not None:
            try:
                self.stop()
            except:  # noqa: E722
                pass
