resource "aws_db_instance" "fixture" {
  identifier                          = "tigergate-fixture"
  engine                              = "mysql"
  engine_version                      = "5.7"
  instance_class                      = "db.t3.micro"
  allocated_storage                   = 20
  username                            = "admin"
  password                            = "Pr0d-DB-Passw0rd!"   # hard-coded master password
  publicly_accessible                 = true
  storage_encrypted                   = false
  backup_retention_period             = 0
  multi_az                            = false
  deletion_protection                 = false
  skip_final_snapshot                 = true
  auto_minor_version_upgrade          = false
  iam_database_authentication_enabled = false
  monitoring_interval                 = 0
  vpc_security_group_ids              = [aws_security_group.wide_open.id]
  # no enabled_cloudwatch_logs_exports, no performance insights
}

resource "aws_rds_cluster" "aurora" {
  cluster_identifier      = "tigergate-aurora"
  engine                  = "aurora-mysql"
  master_username         = "admin"
  master_password         = "Aur0ra-Passw0rd!"
  storage_encrypted       = false
  backup_retention_period = 1
  skip_final_snapshot     = true
  deletion_protection     = false
}

resource "aws_elasticache_cluster" "redis" {
  cluster_id      = "tigergate-cache"
  engine          = "redis"
  node_type       = "cache.t3.micro"
  num_cache_nodes = 1
  # no at-rest / in-transit encryption (replication group required for that), no auth token
}

resource "aws_dynamodb_table" "fixture" {
  name         = "tigergate-fixture"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "id"
  attribute {
    name = "id"
    type = "S"
  }
  # no server_side_encryption, no point_in_time_recovery
}

resource "aws_sqs_queue" "fixture" {
  name = "tigergate-fixture"
  # no kms_master_key_id
}

resource "aws_sns_topic" "fixture" {
  name = "tigergate-fixture"
  # no kms_master_key_id
}

resource "aws_efs_file_system" "fixture" {
  encrypted = false
}
