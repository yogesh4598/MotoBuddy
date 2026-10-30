<?php
require_once __DIR__ . "/../../../config/db.php";
require_once __DIR__ . "/../../../config/auth_helper.php";

$userId = getAuthenticatedUserId($conn);

if (isset($conn) && $conn !== null) {
    $stmt = $conn->prepare("SELECT id, name, email FROM users WHERE id = :id");
    $stmt->bindParam(":id", $userId);
    $stmt->execute();

    if ($stmt->rowCount() > 0) {
        $user = $stmt->fetch(PDO::FETCH_ASSOC);
        echo json_encode([
            "success" => true,
            "message" => "User profile retrieved",
            "data" => [
                "id" => (string)$user['id'],
                "name" => $user['name'],
                "email" => $user['email']
            ]
        ]);
        exit();
    }
}

echo json_encode([
    "success" => true,
    "message" => "User profile",
    "data" => [
        "id" => (string)$userId,
        "name" => "Aarav Sharma",
        "email" => "aarav.sharma@demo.com"
    ]
]);
?>
