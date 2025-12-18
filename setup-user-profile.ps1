$KEYCLOAK_URL = "http://localhost:8081"
$REALM = "area-check"
$ADMIN_USER = "admin"
$ADMIN_PASS = "admin"

Write-Host ""
Write-Host "Getting admin token..." -ForegroundColor Yellow

try {
    $tokenResponse = Invoke-RestMethod -Uri "$KEYCLOAK_URL/realms/master/protocol/openid-connect/token" `
        -Method Post `
        -ContentType "application/x-www-form-urlencoded" `
        -Body "username=$ADMIN_USER&password=$ADMIN_PASS&grant_type=password&client_id=admin-cli"
    
    $TOKEN = $tokenResponse.access_token
    
    if (-not $TOKEN) {
        Write-Host "ERROR: Could not get token!" -ForegroundColor Red
        exit 1
    }
    
    Write-Host "Token received" -ForegroundColor Green
}
catch {
    Write-Host "ERROR: Cannot connect to Keycloak at $KEYCLOAK_URL" -ForegroundColor Red
    Write-Host "Run: docker-compose -f docker-compose.keycloak.yml up -d" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "Enabling User Profile..." -ForegroundColor Yellow

try {
    Invoke-RestMethod -Uri "$KEYCLOAK_URL/admin/realms/$REALM" `
        -Method Put `
        -Headers @{
            Authorization = "Bearer $TOKEN"
            "Content-Type" = "application/json"
        } `
        -Body '{"attributes":{"userProfileEnabled":"true"}}' | Out-Null
    
    Write-Host "User Profile enabled" -ForegroundColor Green
}
catch {
    Write-Host "WARNING: Could not enable User Profile (maybe already enabled)" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Configuring maxRadius attribute..." -ForegroundColor Yellow

$userProfileConfig = @{
    attributes = @(
        @{
            name = "username"
            displayName = "`${username}"
            validations = @{
                length = @{ min = 3; max = 255 }
                "username-prohibited-characters" = @{}
                "up-username-not-idn-homograph" = @{}
            }
            permissions = @{
                view = @("admin", "user")
                edit = @("admin", "user")
            }
            multivalued = $false
        },
        @{
            name = "email"
            displayName = "`${email}"
            validations = @{
                email = @{}
                length = @{ max = 255 }
            }
            required = @{
                roles = @("user")
            }
            permissions = @{
                view = @("admin", "user")
                edit = @("admin", "user")
            }
            multivalued = $false
        },
        @{
            name = "firstName"
            displayName = "`${firstName}"
            validations = @{
                length = @{ max = 255 }
                "person-name-prohibited-characters" = @{}
            }
            permissions = @{
                view = @("admin", "user")
                edit = @("admin", "user")
            }
            multivalued = $false
        },
        @{
            name = "lastName"
            displayName = "`${lastName}"
            validations = @{
                length = @{ max = 255 }
                "person-name-prohibited-characters" = @{}
            }
            permissions = @{
                view = @("admin", "user")
                edit = @("admin", "user")
            }
            multivalued = $false
        },
        @{
            name = "maxRadius"
            displayName = "Maximum Radius (R)"
            validations = @{
                length = @{ min = 1; max = 10 }
            }
            annotations = @{
                inputType = "text"
                inputHelperTextBefore = "Enter a value between 0.5 and 5.0"
            }
            required = @{
                roles = @("user")
            }
            permissions = @{
                view = @("admin", "user")
                edit = @("admin", "user")
            }
            multivalued = $false
        }
    )
    groups = @(
        @{
            name = "user-metadata"
            displayHeader = "User metadata"
            displayDescription = "Attributes, which refer to user metadata"
        }
    )
}

$body = $userProfileConfig | ConvertTo-Json -Depth 10

try {
    Invoke-RestMethod -Uri "$KEYCLOAK_URL/admin/realms/$REALM/users/profile" `
        -Method Put `
        -Headers @{
            Authorization = "Bearer $TOKEN"
            "Content-Type" = "application/json"
        } `
        -Body $body | Out-Null
    
    Write-Host "User Profile configured!" -ForegroundColor Green
}
catch {
    Write-Host "ERROR configuring User Profile:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host "SUCCESS! User Profile with maxRadius is configured" -ForegroundColor Green
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Test registration:" -ForegroundColor Yellow
Write-Host "  1. Open: http://localhost:3000" -ForegroundColor White
Write-Host "  2. Click: 'Login via Keycloak' -> 'Register'" -ForegroundColor White
Write-Host "  3. Field 'Maximum Radius (R)' should be present" -ForegroundColor White
Write-Host ""
Write-Host "Test users:" -ForegroundColor Yellow
Write-Host "  user1 / user1   (maxRadius: 1.5)" -ForegroundColor White
Write-Host "  demo / demo     (maxRadius: 2.0)" -ForegroundColor White
Write-Host "  admin / admin   (maxRadius: 5.0)" -ForegroundColor White
Write-Host ""
