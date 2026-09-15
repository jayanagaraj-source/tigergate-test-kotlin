resource "aws_vpc" "fixture" {
  cidr_block = "10.0.0.0/16"
  # no flow logs
}

resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.fixture.id
  cidr_block              = "10.0.1.0/24"
  map_public_ip_on_launch = true
}

# Every port open to the world in both directions.
resource "aws_security_group" "wide_open" {
  name        = "tigergate-wide-open"
  description = "Deliberately permissive security group"
  vpc_id      = aws_vpc.fixture.id

  ingress {
    description = "SSH from anywhere"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }
  ingress {
    description = "RDP from anywhere"
    from_port   = 3389
    to_port     = 3389
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    description = "Database from anywhere"
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    description = "All traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_network_acl" "open" {
  vpc_id = aws_vpc.fixture.id
  ingress {
    protocol   = "-1"
    rule_no    = 100
    action     = "allow"
    cidr_block = "0.0.0.0/0"
    from_port  = 0
    to_port    = 0
  }
}
