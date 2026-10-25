using Grpc.Core;
using Grpc.Net.Client;
using PermissionService.Api.Grpc.V1;
using PermissionService.Domain;

namespace PermissionService.IntegrationTests;

// Çalışan host gerekir: önce `dotnet run --project src/Host`
public class GrpcCheckTests
{
    [Fact]
    public async Task Check_over_grpc_matches_spicedb_result()
    {
        using var store = SpiceDbStoreTests.CreateStore();
        var id = Guid.NewGuid().ToString("N");

        var token = await store.WriteAsync(
        [
            new(UpdateOp.Touch, new(new ObjectRef("project", $"p{id}"), "view_granted",
                new SubjectRef(new ObjectRef("user", $"u{id}")))),
        ], default);

        using var channel = GrpcChannel.ForAddress("http://localhost:9085");
        var client = new PermissionCheck.PermissionCheckClient(channel);

        var allowed = await client.CheckAsync(Request(id, "view", token));
        Assert.True(allowed.Allowed);

        var denied = await client.CheckAsync(Request(id, "manage_members", token));
        Assert.False(denied.Allowed);
    }

    [Fact]
    public async Task Invalid_request_returns_InvalidArgument()
    {
        using var channel = GrpcChannel.ForAddress("http://localhost:9085");
        var client = new PermissionCheck.PermissionCheckClient(channel);

        var ex = await Assert.ThrowsAsync<RpcException>(() =>
            client.CheckAsync(new CheckRequest { Permission = "view" }).ResponseAsync);

        Assert.Equal(StatusCode.InvalidArgument, ex.StatusCode);
    }

    private static CheckRequest Request(string id, string permission, string token) => new()
    {
        Subject = new SubjectReference { Object = new ObjectReference { Type = "user", Id = $"u{id}" } },
        Permission = permission,
        Resource = new ObjectReference { Type = "project", Id = $"p{id}" },
        ConsistencyToken = token,
    };
}