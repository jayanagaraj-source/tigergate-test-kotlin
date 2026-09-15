package com.tigergate.fixture

// Fake credentials shaped to match common secret-scanner signatures. None of these are real or were ever valid.
object Secrets {
    const val API_KEY = "test-fixture-not-a-real-secret"

    // CWE-798 / CWE-259
    const val AWS_ACCESS_KEY_ID = "AKIAIOSFODNN7EXAMPLE"
    const val AWS_SECRET_ACCESS_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
    const val GITHUB_TOKEN = "ghp_hvGQfZzrTul83lLuOF8Vmk2SDtDrj2O61LUA"
    const val SLACK_BOT_TOKEN = "xoxb-573877308390-0993003536138-nq4auvSzWzEmr1y8R09QLqar"
    const val STRIPE_SECRET_KEY = "sk_live_6oT7EtJAbV3wtiL00FkYWV18"
    const val GOOGLE_API_KEY = "AIzaT2QEDbXQdXUcFUsvJwF57XlMQvY5OzBsayU"
    const val SENDGRID_API_KEY = "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"
    const val JWT_SIGNING_SECRET = "j0Vm6nOkRs6sULg60pn31p7n0HoD6LEpHG16onyxlnmxa0h0"
    const val ADMIN_PASSWORD = "Sup3rS3cret!Admin"

    // CWE-798 credentials embedded in connection URLs.
    const val DATABASE_URL = "postgres://app_admin:Pr0d-DB-Passw0rd!@db.internal:5432/app"
    const val GIT_REMOTE = "https://deploy:Dep10y-T0ken-2024@git.internal/platform/app.git"

    // CWE-321 private key committed to source.
    val PRIVATE_KEY = """
-----BEGIN RSA PRIVATE KEY-----
MIIEpAIBAAKCAQEA3SMu516A0j9QStNk7+RNFSnKbNeGQzF3QZxF8bG/hwFKdoi9
PO3ZemiTyhRAf9k89Q4bdG5atbsSE5kEX4RzFYDCyX463jL0P3AF309uTJTH72Q2
RG6Fg+uu4e0y87oX/rPc5ey4DgyusaIsRV8NZA86xylVMRC93GdS3y9llxhb4rbh
zu/xdYJVHZpeO9bJ5KLIxLyov0xeSt5HZNHYWbDG6PjnrkmmeafYW1GWaXalt2se
D6ofOWscalh68V6khHtLtrsME9zwN1wAGIdF91gKHYI79GSPnArZ6Nfm+KvM3BWv
RLPngs602ummXFCt7TPAEZVRN1twbvdmx2aeLwIDAQABAoIBABpOgthACXUkK7TA
ZyPpSzOMUCoYjZQAFh7LrL/EPx5Gt16t2k1IQknp7O2U8S4EYC21gECeltNDA4h/
TeI1tcxwupC7yob/wBytLUBT3lw6is+zYof2Lz3yqKWfJnjmF3qpAktRQ31bDDZ8
2Lq77weLOZkoapu2pnofdIqxGcBv4GpY9/PZ0U5m4KSkVO2t+QOuVSsBMY4ThdA4
tNNd1AsWubRdJe/kPIj/vdJREqAT7wPnGPqpFvEGZiGRDiAi1fVY0GUKzjOZIcPR
dUsjdlnAbPY44oZAVFfLWuo66nZap2nE+axZa5M6n5TpSL4qLp+OAxVSX+16TKkG
VugZTikCgYEA5PqjQcVtW135UJNZXlj8n1j7SET6P+t4n6+BwSzS3L7ZU4zpHlIr
3e4//1iaPvs74m3H4xSLHj4XW9wGia2cV3CDx1p0VsNEF0pRCQAeEI+QTGASxjpw
hv4AMbnYTvYe4svQvmW4WV5UfPyZV2Qv+/8q2vPbgfolBfUFmuZ/8t0CgYEA9zun
R08028M6djN4JS0slWIYMF5G2fA9Ff3ike2GRn7URbb3pGsHc62DW/KQegNJUSqM
mW+e7IKfOdeU+IETb4RT+Dt3bcYgu4R8YH13VXqleEgYfygiNBDxzHseIWAx2186
HMXhyBDQJ/gdwKHvkYeFnihZgSNfyOFHfVIOxnsCgYArgmilJcOa2ed25RjVMamH
CdxtphW7MZ4xEaNhNog2w9j95EejqE+Hky778TUWBJ0jcGLMBmpM69V6cfuYC7Iq
d0nDBrAWkHIOzuNJB4mu8eikapkGq7IYJIma6Upt7px90eavVxrJeIArY8Ard/Ea
PClDg5TRRNvmHuHaxjPHzQKBgQD2pfa+t5B+Zoief76vKCBAb/q91nBM4NWwaPdu
qk65abKQUndA+t4WU9anFpl4gGrWpOJb+PXN8oqzhBXEzdD8AkJbdcgyJeVl7ki/
Yqx5BMCr/REjWoC0/OqeG331wig7OQeayPf1RoDEP04/bc65m0B8hri0kvvuRoGx
zvmanQKBgQDVvnR17AXJOxOphCuZPYVDNgH0hFUzhraf2JEkqGOD3E5OIgkWIY0J
GOh2ywK23kaa/KrYifoJcKCCvJNm5lDSCPI7x+AxnVvbpWVKRvDeyf/VsbWtxewn
Z6NkDmrU61ZEOjE4w0DjrmL7aP6z6aG9BPfnshLFD5dXpfSL5wiBlA==
-----END RSA PRIVATE KEY-----
""".trimIndent()
}
