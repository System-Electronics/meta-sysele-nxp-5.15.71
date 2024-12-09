# meta-layer/recipes-core/os-release-sign/os-release-sign.bb

SUMMARY = "Secure OS release signature information"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# Required native tools
DEPENDS += "openssl-native python3-native"

# Environment variables passed from GitHub Actions
export GITHUB_SHA ?= "unknown"
export GITHUB_REPOSITORY ?= "unknown"
export GITHUB_RUN_ID ?= "unknown"
export BUILD_SIGNATURE ?= "unofficial"

# Include verification tool in the image
SRC_URI = "file://verify_build.py"

python do_install() {
    import os
    import json
    import yaml
    import hashlib
    from datetime import datetime
    
    # Create installation directory
    target_dir = d.getVar('D') + "/opt/systele"
    os.makedirs(target_dir, exist_ok=True)
    
    # Collect all build information
    build_info = {
        'build_type': 'official' if d.getVar('BUILD_SIGNATURE') != 'unofficial' else 'custom',
        'build_timestamp': datetime.utcnow().isoformat(),
        'git_commit': d.getVar('GITHUB_SHA'),
        'git_repository': d.getVar('GITHUB_REPOSITORY'),
        'github_run_id': d.getVar('GITHUB_RUN_ID'),
        'yocto_version': d.getVar('DISTRO_VERSION')
    }
    
    # Create tamper-proof data structure
    protected_data = build_info.copy()
    protected_data['signature'] = d.getVar('BUILD_SIGNATURE')
    
    # Generate cryptographic checksum for tamper detection
    protected_json = json.dumps(protected_data, sort_keys=True)
    checksum = hashlib.sha256(protected_json.encode()).hexdigest()
    
    # Add integrity verification data
    build_info['data_checksum'] = checksum
    build_info['signature'] = d.getVar('BUILD_SIGNATURE')
    
    # Write the final YAML file
    with open(os.path.join(target_dir, 'os-release.yaml'), 'w') as f:
        yaml.dump(build_info, f, default_flow_style=False)
        
    # Install verification script with executable permissions
    import shutil
    workdir = d.getVar('WORKDIR')
    shutil.copy2(os.path.join(workdir, 'verify_build.py'), 
                os.path.join(target_dir, 'verify_build.py'))
    os.chmod(os.path.join(target_dir, 'verify_build.py'), 0o755)
}

# Define files to be included in the package
FILES:${PN} += "/opt/systele/os-release.yaml /opt/systele/verify_build.py"
