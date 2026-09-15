resource "google_storage_bucket" "public" {
  name          = "tigergate-fixture-public"
  location      = "US"
  force_destroy = true
  # uniform_bucket_level_access not enabled, no versioning, no logging
}

resource "google_storage_bucket_iam_member" "all_users" {
  bucket = google_storage_bucket.public.name
  role   = "roles/storage.objectViewer"
  member = "allUsers"
}

resource "google_compute_firewall" "allow_all" {
  name    = "tigergate-allow-all"
  network = "default"
  allow {
    protocol = "tcp"
    ports    = ["0-65535"]
  }
  source_ranges = ["0.0.0.0/0"]
}

resource "google_compute_instance" "fixture" {
  name         = "tigergate-fixture"
  machine_type = "e2-micro"
  zone         = "us-central1-a"
  boot_disk {
    initialize_params { image = "debian-cloud/debian-9" }
  }
  network_interface {
    network = "default"
    access_config {}                       # public IP
  }
  metadata = {
    enable-oslogin           = "false"
    block-project-ssh-keys   = "false"
    serial-port-enable       = "true"
    startup-script           = "export DB_PASSWORD=Pr0d-DB-Passw0rd!"
  }
  service_account {
    email  = "default"
    scopes = ["cloud-platform"]            # full-access default SA
  }
  # no shielded_instance_config
}

resource "google_sql_database_instance" "fixture" {
  name             = "tigergate-fixture"
  database_version = "MYSQL_5_6"
  region           = "us-central1"
  settings {
    tier = "db-f1-micro"
    ip_configuration {
      ipv4_enabled = true
      require_ssl  = false
      authorized_networks {
        name  = "everyone"
        value = "0.0.0.0/0"
      }
    }
    backup_configuration { enabled = false }
  }
}

resource "google_container_cluster" "fixture" {
  name               = "tigergate-fixture"
  location           = "us-central1"
  initial_node_count = 1
  enable_legacy_abac = true
  master_auth {
    username = "admin"
    password = "Gke-Adm1n-Passw0rd-1234567890"
    client_certificate_config { issue_client_certificate = true }
  }
  # no private cluster, no network policy, no master authorized networks, no binary authorization
}
