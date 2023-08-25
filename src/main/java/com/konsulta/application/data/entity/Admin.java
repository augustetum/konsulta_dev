package com.konsulta.application.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "kon_admins")
public class Admin extends Account{
}
