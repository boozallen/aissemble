// Enable UI
ui = true
disable_mlock = true

// Filesystem storage
storage "file" {
path = "/vault/data"
}

// TCP Listener
listener "tcp" {
address = "0.0.0.0:8200"
tls_disable = "true"
}