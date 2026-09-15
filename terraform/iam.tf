# Wildcard IAM policy attached to a user with a long-lived access key.
resource "aws_iam_policy" "admin_everything" {
  name   = "tigergate-admin-everything"
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect   = "Allow"
      Action   = "*"
      Resource = "*"
    }]
  })
}

resource "aws_iam_user" "ci" {
  name = "tigergate-ci"
}

resource "aws_iam_access_key" "ci" {
  user = aws_iam_user.ci.name
}

resource "aws_iam_user_policy_attachment" "ci_admin" {
  user       = aws_iam_user.ci.name
  policy_arn = aws_iam_policy.admin_everything.arn
}

resource "aws_iam_user_policy_attachment" "ci_managed_admin" {
  user       = aws_iam_user.ci.name
  policy_arn = "arn:aws:iam::aws:policy/AdministratorAccess"
}

resource "aws_iam_role" "lambda" {
  name = "tigergate-lambda"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Principal = { AWS = "*" }          # anyone can assume
      Action    = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role_policy" "lambda_inline" {
  name = "tigergate-lambda-inline"
  role = aws_iam_role.lambda.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      { Effect = "Allow", Action = ["s3:*", "iam:PassRole", "iam:*"], Resource = "*" },
      { Effect = "Allow", Action = "kms:Decrypt", Resource = "*" },
    ]
  })
}

resource "aws_iam_account_password_policy" "weak" {
  minimum_password_length        = 6
  require_lowercase_characters   = false
  require_numbers                = false
  require_uppercase_characters   = false
  require_symbols                = false
  allow_users_to_change_password = true
  max_password_age               = 0
  password_reuse_prevention      = 0
}
