#!/usr/bin/env python3

import yaml
import json
import hashlib
import sys
from typing import Dict, Tuple

def verify_build_integrity(filepath: str) -> Tuple[bool, str]:
    """
    Verify the integrity of a build release file.
    
    Args:
        filepath: Path to the os-release.yaml file
        
    Returns:
        Tuple[bool, str]: (is_valid, message)
            - is_valid: True if the file is authentic and unmodified
            - message: Detailed verification result or error message
    """
    try:
        # Load and parse YAML file
        with open(filepath, 'r') as f:
            data = yaml.safe_load(f)
        
        # Extract stored checksum and remove it from verification
        stored_checksum = data.pop('data_checksum', None)
        if not stored_checksum:
            return False, "Missing checksum - possible tampering detected"
            
        # Reconstruct protected dataset for verification
        protected_data = {
            'build_type': data.get('build_type'),
            'build_timestamp': data.get('build_timestamp'),
            'git_commit': data.get('git_commit'),
            'git_repository': data.get('git_repository'),
            'github_run_id': data.get('github_run_id'),
            'yocto_version': data.get('yocto_version'),
            'signature': data.get('signature')
        }
        
        # Calculate verification checksum
        protected_json = json.dumps(protected_data, sort_keys=True)
        calculated_checksum = hashlib.sha256(protected_json.encode()).hexdigest()
        
        # Verify data integrity
        if calculated_checksum != stored_checksum:
            return False, "Checksum mismatch - file has been modified"
            
        # Additional validation for official builds
        if data.get('build_type') == 'official':
            if not data.get('signature') or data.get('signature') == 'unofficial':
                return False, "Invalid signature for official build"
                
        return True, "Verification completed successfully"
        
    except Exception as e:
        return False, f"Verification error: {str(e)}"

if __name__ == "__main__":
    # Command-line interface
    if len(sys.argv) != 2:
        print("Usage: verify_build.py <path/to/os-release.yaml>")
        sys.exit(1)
        
    # Perform verification and exit with appropriate status code
    is_valid, message = verify_build_integrity(sys.argv[1])
    print(f"Verification status: {message}")
    sys.exit(0 if is_valid else 1)
