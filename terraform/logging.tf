resource "aws_kms_key" "fixture" {
  description         = "fixture key"
  enable_key_rotation = false
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{ Effect = "Allow", Principal = { AWS = "*" }, Action = "kms:*", Resource = "*" }]
  })
}

resource "aws_cloudtrail" "fixture" {
  name                          = "tigergate-fixture"
  s3_bucket_name                = aws_s3_bucket.public_fixture.id
  include_global_service_events = false
  is_multi_region_trail         = false
  enable_log_file_validation    = false
  # no kms_key_id, no cloud_watch_logs_group_arn
}

resource "aws_cloudwatch_log_group" "fixture" {
  name = "/tigergate/fixture"
  # no retention_in_days, no kms_key_id
}

resource "aws_ecr_repository" "fixture" {
  name                 = "tigergate-fixture"
  image_tag_mutability = "MUTABLE"
  image_scanning_configuration { scan_on_push = false }
}

resource "aws_api_gateway_rest_api" "fixture" {
  name = "tigergate-fixture"
}

resource "aws_api_gateway_stage" "fixture" {
  rest_api_id   = aws_api_gateway_rest_api.fixture.id
  stage_name    = "prod"
  deployment_id = "fixture"
  # no access_log_settings, no client_certificate_id, no xray_tracing, no WAF
}
