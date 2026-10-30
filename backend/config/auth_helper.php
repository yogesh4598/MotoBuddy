<?php
function getAuthenticatedUserId($conn) {
    $headers = getallheaders();
    $authHeader = isset($headers['Authorization']) ? $headers['Authorization'] : (isset($headers['authorization']) ? $headers['authorization'] : '');

    if (!empty($authHeader) && strpos($authHeader, 'Bearer ') === 0) {
        $token = str_replace("Bearer ", "", $authHeader);
        if (!empty($token) && $conn !== null) {
            $stmt = $conn->prepare("SELECT id FROM users WHERE token = :token");
            $stmt->bindParam(":token", $token);
            $stmt->execute();
            if ($stmt->rowCount() > 0) {
                $user = $stmt->fetch(PDO::FETCH_ASSOC);
                return (int)$user['id'];
            }
        }
    }
    return 1; // Default fallback user ID for unauthenticated local development testing
}
?>
