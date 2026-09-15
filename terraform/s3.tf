# Public, unencrypted, unversioned, unlogged bucket.
resource "aws_s3_bucket" "public_fixture" {
  bucket = "tigergate-test-kotlin-public-fixture"
  acl    = "public-read"
  # no server_side_encryption_configuration, versioning, logging, or lifecycle blocks
}

resource "aws_s3_bucket_public_access_block" "public_fixture" {
  bucket                  = aws_s3_bucket.public_fixture.id
  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

resource "aws_s3_bucket_policy" "public_fixture" {
  bucket = aws_s3_bucket.public_fixture.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Sid       = "AllowEveryone"
      Effect    = "Allow"
      Principal = "*"
      Action    = "s3:*"
      Resource  = ["${aws_s3_bucket.public_fixture.arn}", "${aws_s3_bucket.public_fixture.arn}/*"]
    }]
  })
}
