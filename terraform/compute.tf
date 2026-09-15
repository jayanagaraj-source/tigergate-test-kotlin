resource "aws_instance" "fixture" {
  ami                         = "ami-0c55b159cbfafe1f0"
  instance_type               = "t3.micro"
  subnet_id                   = aws_subnet.public.id
  vpc_security_group_ids      = [aws_security_group.wide_open.id]
  associate_public_ip_address = true
  monitoring                  = false
  ebs_optimized               = false

  root_block_device {
    encrypted = false
  }

  # IMDSv1 left enabled.
  metadata_options {
    http_endpoint = "enabled"
    http_tokens   = "optional"
  }

  # Secrets in user data.
  user_data = <<-USERDATA
    #!/bin/bash
    export DB_PASSWORD="Pr0d-DB-Passw0rd!"
    export AWS_SECRET_ACCESS_KEY="wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
    curl -k https://releases.internal/install.sh | bash
  USERDATA
}

resource "aws_ebs_volume" "unencrypted" {
  availability_zone = "us-east-1a"
  size              = 20
  encrypted         = false
}

resource "aws_launch_configuration" "fixture" {
  name_prefix   = "tigergate-"
  image_id      = "ami-0c55b159cbfafe1f0"
  instance_type = "t3.micro"
  associate_public_ip_address = true
  root_block_device { encrypted = false }
  user_data = "echo DB_PASSWORD=Pr0d-DB-Passw0rd!"
}

resource "aws_lambda_function" "fixture" {
  function_name = "tigergate-fixture"
  role          = aws_iam_role.lambda.arn
  handler       = "index.handler"
  runtime       = "python3.6"            # EOL runtime
  filename      = "lambda.zip"
  environment {
    variables = {
      DB_PASSWORD = "Pr0d-DB-Passw0rd!"  # secret in env, no KMS
      API_KEY     = "AKIAIOSFODNN7EXAMPLE"
    }
  }
  # no tracing_config, no dead_letter_config, no vpc_config
}
