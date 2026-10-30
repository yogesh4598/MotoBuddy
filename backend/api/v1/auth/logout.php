<?php
require_once __DIR__ . "/../../../config/db.php";

$headers = getallheaders();
$authHeader = isset($headers['Authorization']) ? $headers['Authorization'] : (isset($headers['authorization']) ? $headers['authorization'] : '');

if (isset($conn) && $conn !== null && !empty($authHeader)) {
    $token = str_replace("Bearer ", "", $authHeader);
    if (!empty($token)) {
        $stmt = $conn->prepare("UPDATE users SET token = NULL WHERE token = :token");
        $stmt->bindParam(":token", $token);
        $stmt->execute();
    }
}

echo json_encode([
    "success" => true,
    "message" => "Logged out successfully"
]);
?>
